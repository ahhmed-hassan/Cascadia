package service

import entity.*
import java.io.File

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService : RootService) : AbstractRefreshingService() {

    /**
     * Initializes and starts a new game of Cascadia with the specified players and scoring rules.
     * This funktion sets up a new game session in either "Hotseat" or "Network". This is determined by the existence
     * of a network player.
     *
     * The function initializes the Players, HabitatTiles, shop, ruleSet, order, startTiles and the wildlifeTokens.
     *
     * If the game is a network Game some of this information has to come from the host or if this game instance
     * is the host they have to be communicated to the other clients.
     *
     * @param playerNames a map for player names, and "playerType" determines if the player is a bot, local player or
     * a network player.
     * @param scoreRules a list of boolean representing the selected scoring rules. false for Cards A. true for Cards B.
     * null for random rules.
     * @param orderIsRandom determines if the order should be random or the order from the playerNames
     * @param isRandomRules determines whether the scoring rules should be randomized or provided explicitly.
     * @param startTileOrder a list of integers representing the indices of starting tiles.
     *
     * @throws IllegalArgumentException if The number of players is not between 2 and 4 or Player names are not unique
     * or scoring rule list does not have 5 entries.
     * @throws IllegalArgumentException if it is a network game, there has to be exactly one local player
     */
    fun startNewGame(
        playerNames : Map<String,PlayerType>,
        scoreRules : List<Boolean>,
        orderIsRandom: Boolean,
        isRandomRules: Boolean,
        startTileOrder: List<Int>? = null
    ) {
        require(playerNames.size in 2 .. 4) { "The number of players must be between 2 and 4" }

        //Check the size of the rules and determine if they are randomized or provided by the user
        if(isRandomRules) {
            require(scoreRules.size == 5) { "The scoring rules must be 5" }
        } else {
            // true is 1 (Cards B), false is 0 (Cards A)
            val randomRules = List(5) { (0..1).random() == 1 }
        }

        //Player names must be unique,
        // size of the original key list must match the size of the unique set of keys.
        require(playerNames.keys.size == playerNames.keys.toSet().size) { "Player names must be unique." }

        //There is exactly one local player in a network game
        //Count the number of players with type "LOCAL"
        val localPlayers = playerNames.values.count { it == PlayerType.LOCAL }
        if(localPlayers != 1) {
            throw IllegalArgumentException("In a network game must be exactly one local player.")
        }

        //Ensure that no network players exist if the game connection state indicates Hotseat mode
        val networkService = NetworkService(rootService)
        if(networkService.connectionState == ConnectionState.DISCONNECTED) {
            val networkPlayers = playerNames.values.count { it == PlayerType.NETWORK }
            if (networkPlayers > 0) {
                throw IllegalArgumentException("In a Hotseat game, no player can be of type NETWORK.")
            }
        }

        //Determine the player order based on the orderIsRandom parameter
        val playerOrder = if(orderIsRandom) {
            playerNames.keys.shuffled()
        } else {
            playerNames.keys.toList()
        }

        //Habitat tile distribution according to the number of players
        val totalTiles = when (playerNames.size) {
            2 -> 43
            3 -> 63
            4 -> 83
            else -> throw IllegalArgumentException("Invalid number of players, player count must be between 2 and 4")
        }

        // Load Habitat Tiles
        val habitatTiles = getHabitatTiles().toMutableList()
        habitatTiles.shuffle()

        //Limit the habitatTiles list to the required number of tiles
        val totalTilesInGame = habitatTiles.take(totalTiles).toMutableList()

        //Create WildLifeTokens (20 each of Bear, Elk, Salmon, Hawk, Fox)
        val wildlifeTokens = mutableListOf<WildlifeToken>()
        repeat(20) {
            wildlifeTokens.add(WildlifeToken(Animal.BEAR))
            wildlifeTokens.add(WildlifeToken(Animal.ELK))
            wildlifeTokens.add(WildlifeToken(Animal.FOX))
            wildlifeTokens.add(WildlifeToken(Animal.HAWK))
            wildlifeTokens.add(WildlifeToken(Animal.SALMON))
        }
        wildlifeTokens.shuffle()

        // Create shop with first 4 tiles and first 4 wildlife tokens
        val shop = totalTilesInGame.take(4).mapIndexed { index, tile ->
            tile as HabitatTile? to wildlifeTokens[index] as WildlifeToken?
        }.toMutableList()
        //Remove the used habitat tiles and wildlife tokens from the main list.
        totalTilesInGame.removeAll(shop.map { it.first })
        wildlifeTokens.removeAll(shop.map { it.second })

        // Load Start Tiles
        val startTiles = getStartTiles().toMutableList()
        startTiles.shuffle()

        // Create player list
        val playerList = playerNames.map { (name, type) ->
            Player(name, mutableMapOf(), type)
        }

        //Create the game
        val game = CascadiaGame(
            startTiles,
            scoreRules,
            0.3f,
            25,
            false,
            false,
            shop,
            mutableListOf(),
            playerList.first(),
            playerList,
            totalTilesInGame,
            null,
            null,
            wildlifeTokens
        )

        // Resolve overpopulation of four in the shop after game created
        if(checkForSameAnimal()) {
            resolveOverpopulation()
        }

        // This block is only activated if the game is a network game and startTileOrder is provided.
        if (startTileOrder != null && startTileOrder.size == playerList.size) {
            for (i in playerList.indices) {
                val tileIndex = startTileOrder[i]  // e.g., 2 => startTiles[2]
                val player = playerList[i]         // i-th player
                // Retrieve the starting tiles assigned to the player based on the tile index.
                val playerStartTile = startTiles[tileIndex]

                //Place the top tile in the player's habitat (central)
                player.habitat[0 to 0] = playerStartTile[0]
                //Place the lower-right tile in the player's habitat
                player.habitat[1 to -1] = playerStartTile[1]
                //Place the lower-left tile in the player's habitat
                player.habitat[1 to 0] = playerStartTile[2]
            }
        } else {
            for(i in playerList.indices) {
                //Retrieve the player's name based on the pre-determined player order.
                //then, find the corresponding Player object from the player list
                //and retrieve the associated starting habitat tiles for this player.
                val playerName = playerOrder[i]
                val player = playerList.first { it.name == playerName }
                val playerStartTile = startTiles[i]

                //Place the top tile in the player's habitat (central)
                player.habitat[0 to 0] = playerStartTile[0]
                //Place the lower-right tile in the player's habitat
                player.habitat[1 to -1] = playerStartTile[1]
                //Place the lower-left tile in the player's habitat
                player.habitat[1 to 0] = playerStartTile[2]
            }
        }

        rootService.currentGame = game
        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * Reads habitat tiles from the `tiles.csv` file and returns a list of `HabitatTile` objects.
     */
    fun getHabitatTiles(): List<HabitatTile> {
        val habitatTiles = mutableListOf<HabitatTile>()
        File("tiles.csv").bufferedReader().useLines { lines ->
            lines.drop(1) //skip the first line (header)
                .filter { it.isNotBlank() } //exclude empty lines
                .filterNot { it.contains("--", ignoreCase = true) } //exclude lines containing "--" (-- seite)
                .forEach{ line ->
                    val part = line.split(";")  //Parse data from the CSV line
                    val id = part[0].toInt()
                    val habitats = part[1].map { Terrain.valueOf(it.toString()) }.toMutableList()
                    val wildlife = part[2].map { Animal.valueOf(it.toString()) }
                    val keystone = part[3].toBoolean()

                    //Add a new HabitatTile to the list
                    habitatTiles.add(HabitatTile(
                        id,
                        keystone,
                        0,
                        wildlife,
                        null,
                        habitats))
                }
        }
        return habitatTiles
    }

    /**
     * Reads starting tiles for players from the `start_tiles.csv` file and returns a list of tile groups.
     */
    fun getStartTiles(): List<List<HabitatTile>> {
        val startTiles = mutableListOf<List<HabitatTile>>() //is List<List<HabitatTile>> in CascadiaGame
        val startTileList = mutableListOf<HabitatTile>() // temp List for startTiles

        File("start_tiles.csv").bufferedReader().useLines { lines ->
            lines.drop(1) //skip the first line (header)
                .forEach { line ->
                    val parts = line.split(";")  //Parse data from the CSV line
                    val id = parts[0].toInt()
                    val habitats = parts[1].map { Terrain.valueOf(it.toString()) }.toMutableList()
                    val wildlife = parts[2].map { Animal.valueOf(it.toString()) }.toMutableList()
                    val keystone = parts[3].toBoolean()

                    //Add a new HabitatTile to the list
                    val startTile = HabitatTile(
                        id,
                        keystone,
                        0,
                        wildlife,
                        null,
                        habitats
                    )
                    startTileList.add(startTile)

                    //once 3 tiles are grouped, add them to the startTiles list and clear the temporary list
                    if(startTileList.size == 3) {
                        startTiles.add(startTileList)
                        startTileList.clear()
                    }
                }
        }
        return startTiles
    }

    /**
     *
     */
    fun nextTurn() {
        //ToDo

        onAllRefreshables { refreshAfterNextTurn() }
    }

}