package service

import entity.*

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
     *
     * @throws IllegalArgumentException if The number of players is not between 2 and 4 or Player names are not unique
     * or scoring rule list does not have 5 entries.
     * @throws IllegalArgumentException if it is a network game, there has to be exactly one local player
     */                                //keys, value
    fun startNewGame(playerNames : Map<String,PlayerType>, scoreRules : List<Boolean>, orderIsRandom: Boolean) {
        require(playerNames.size in 2 .. 4) { "The number of players must be between 2 and 4" }

        //Check the size of the rules and determine if they are randomized or provided by the user
        val isRandomRules = false
        if(isRandomRules) {require(scoreRules.size == 5) { "The scoring rules must be 5" }
        } else {
            // true is 1 (Cards B), false is 0 (Cards A)
            val randomRules = List(5) { (0..1).random() == 1 }
            require(randomRules.size == 5) { "The random scoring rules must be 5" }
        }

        //Player names must be unique,
        // size of the original key list must match the size of the unique set of keys.
        if(playerNames.keys.size != playerNames.keys.toSet().size) {
            throw IllegalArgumentException("Player names must be unique")
        }

        //There is exactly one local player in a network game
        //Count the number of players with type "LOCAL"
        val localPlayers = playerNames.values.count { it == PlayerType.LOCAL }
        if(localPlayers != 1) {
            throw IllegalArgumentException("In a network game must be exactly one local player.")
        }

        //Check that Hotseat Game have no network players
        val networkPlayers = playerNames.values.count { it == PlayerType.NETWORK }
        if(networkPlayers > 0) {
            throw IllegalArgumentException("In a Hotseat game, no player can be of type NETWORK.")
        }

        //Determine the player order based on the orderIsRandom parameter
        if(orderIsRandom) {
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

        val habitatTiles = mutableListOf<HabitatTile>() // Use the Excel Table ?

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

        // Create shop with first 5 Tiles
        val shop = habitatTiles.take(5).mapIndexed { index, tile ->
            tile to wildlifeTokens[index]
        }.toMutableList()
        //Remove the used habitat tiles and wildlife tokens from the main list.
        habitatTiles.removeAll(shop.map { it.first })
        wildlifeTokens.removeAll(shop.map { it.second })

        //Create start tiles (assign 3 starting habitat tiles to each player)
        val startTiles = playerNames.keys.map {
            habitatTiles.take(3).toList().toList()
        }
        //Remove the used starting habitat tiles from the main list.
        habitatTiles.removeAll(startTiles.map { it })    //not working

        // Create player list
        val playerList = playerNames.map { (name, type) ->
            Player(name, emptyMap(), type)
        }

        //Create the game
        val game = CascadiaGame(
            startTiles,             //make it random for each player
            scoreRules,
            0.3f,
            25,             //max Tokens in Game
            false,
            false,
            shop,
            mutableListOf(),
            playerList.first(),
            playerList,
            habitatTiles,
            null,       //CascadiaGame.selectedTile changed
            null,     //CascadiaGame.selectedToken changed
            wildlifeTokens
        )

        val isOverpopulation = false //After game created, if isOverpopulation = true, than change the shop (wildlife)
        //  . . .

        rootService.currentGame = game

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     *
     */
    fun nextTurn() {
        //ToDo

        onAllRefreshables { refreshAfterNextTurn() }
    }

}