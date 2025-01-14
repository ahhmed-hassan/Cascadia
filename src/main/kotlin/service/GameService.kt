package service

import entity.*
import java.io.File

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Initializes and starts a new game of Cascadia with the specified players and scoring rules.
     * This function sets up a new game session in either "Hotseat" or "Network". This is determined by the existence
     * of a network player.
     * The function initializes the Players, HabitatTiles, shop, ruleSet, order, startTiles and the wildlifeTokens.
     * If the game is a network Game some of this information has to come from the host or if this game instance
     * is the host they have to be communicated to the other clients.
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
        playerNames: Map<String, PlayerType>,
        scoreRules: List<Boolean>,
        orderIsRandom: Boolean,
        isRandomRules: Boolean,
        startTileOrder: List<Int>? = null
    ) {
        require(playerNames.size in 2..4) { "The number of players must be between 2 and 4" }

        val ruleSet = if (isRandomRules) { List(5) { (0..1).random() == 1 }} // true is B, false is A
                      else { require(scoreRules.size == 5) { "The scoring rules must be 5" }; scoreRules}

        require(playerNames.keys.size == playerNames.keys.toSet().size) { "Player names must be unique." }

        //Ensure that no network players exist if the game connection state indicates Hotseat mode
        if (rootService.networkService.connectionState == ConnectionState.DISCONNECTED) {
            val networkPlayers = playerNames.values.count { it == PlayerType.NETWORK }
            if (networkPlayers > 0) { throw IllegalArgumentException("In Hotseat, no player can be of type NETWORK.")}}

        val playerOrder = if (orderIsRandom) { playerNames.keys.shuffled() } else { playerNames.keys.toList() }

        //Habitat tile distribution according to the number of players
        val totalTiles = getTileNumber(playerNames.size)
        val habitatTiles = getHabitatTiles().shuffled().toMutableList()
        val totalTilesInGame = habitatTiles.take(totalTiles).toMutableList()

        val wildlifeTokens = createWildlifeToken()

        val shop = totalTilesInGame.take(4).mapIndexed { index, tile ->
            tile as HabitatTile? to wildlifeTokens[index] as WildlifeToken? }.toMutableList()
        totalTilesInGame.removeAll(shop.map { it.first })
        wildlifeTokens.removeAll(shop.map { it.second })

        val startTiles = getStartTiles().toMutableList()

        val playerList = playerOrder.map { name -> val playerType = requireNotNull(playerNames[name])
            Player(name, mutableMapOf(), playerType) }

        val game = CascadiaGame(
            startTiles,
            ruleSet,
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

        rootService.currentGame = game

        if (checkForSameAnimal()) { resolveOverpopulation() }

        // Retrieve the starting tiles assigned to the player based on the tile index.
        if (startTileOrder != null && startTileOrder.size == playerList.size) {
            for (i in playerList.indices) {
                val tileIndex = startTileOrder[i]  // e.g., 2 => startTiles[2]
                val player = playerList[i]         // i-th player
                // Retrieve the starting tiles assigned to the player based on the tile index.
                val playerStartTile = startTiles[tileIndex-1]
                player.habitat[0 to 0] = playerStartTile[0]
                player.habitat[1 to -1] = playerStartTile[1]
                player.habitat[1 to 0] = playerStartTile[2]
            }
        } else {
            for (i in playerList.indices) {
                val playerName = playerOrder[i]
                val player = playerList.first { it.name == playerName }
                val playerStartTile = startTiles[i]
                player.habitat[0 to 0] = playerStartTile[0]
                player.habitat[1 to -1] = playerStartTile[1]
                player.habitat[1 to 0] = playerStartTile[2]
            }
        }
        onAllRefreshables { refreshAfterGameStart() }
    }


    /**
     * create the list of wildLifeToken needed for a Cascadia game
     */
    private fun createWildlifeToken() : MutableList<WildlifeToken> {
        val wildlifeToken = mutableListOf<WildlifeToken>()
        repeat(20) {
            wildlifeToken.add(WildlifeToken(Animal.BEAR))
            wildlifeToken.add(WildlifeToken(Animal.ELK))
            wildlifeToken.add(WildlifeToken(Animal.FOX))
            wildlifeToken.add(WildlifeToken(Animal.HAWK))
            wildlifeToken.add(WildlifeToken(Animal.SALMON))
        }
        wildlifeToken.shuffle()
        return wildlifeToken
    }


    /**
     * estimate the number of habitat tiles depending on the number of players for a Cascadia game
     *
     * @param playerCount is the number of players in the game
     *
     * @throws IllegalArgumentException if PlayerCount not in between 2 and 4
     */
    private fun getTileNumber(playerCount : Int) : Int {
        when (playerCount) {
            2 -> return 43
            3 -> return 63
            4 -> return 83
            else -> throw IllegalArgumentException("Invalid number of players, player count must be between 2 and 4")
        }
    }

    /**
     * Reads starting tiles for players from the "tiles.csv" file and returns a list of tiles.
     *
     * @return the list of habitat tiles from the .csv
     */
    fun getHabitatTiles(): List<HabitatTile> {
        val habitatTiles = mutableListOf<HabitatTile>()
        File("build/resources/main/tiles.csv").bufferedReader().useLines { lines ->
            lines.drop(1) //skip the first line (header)
                .filter { it.isNotBlank() } //exclude empty lines
                .filterNot { it.contains("--", ignoreCase = true) } //exclude lines containing "--" (-- seite)
                .forEach { line ->
                    val part = line.split(";")  //Parse data from the CSV line
                    val id = part[0].toInt()
                    val habitats = part[1].map { Terrain.fromValue(it.toString()) }.toMutableList()
                    val wildlife = part[2].map { Animal.fromValue(it.toString()) }
                    val keystone = when (part[3].lowercase()) {
                        "yes" -> true
                        else -> false
                    }

                    //Add a new HabitatTile to the list
                    habitatTiles.add(
                        HabitatTile(
                            id,
                            keystone,
                            0,
                            wildlife,
                            null,
                            habitats
                        )
                    )
                }
        }
        return habitatTiles
    }

    /**
     * Reads starting tiles for players from the "start_tiles.csv" file and returns a list of tile groups.
     *
     * @return the list of lists of habitat tiles from the .csv each representing one start tile
     */
    private fun getStartTiles(): List<List<HabitatTile>> {
        val startTiles = mutableListOf<List<HabitatTile>>() //is List<List<HabitatTile>> in CascadiaGame
        val startTileList = mutableListOf<HabitatTile>() // temp List for startTiles
        File("build/resources/main/start_tiles.csv").bufferedReader().useLines { lines ->
            lines.drop(1) //skip the first line (header)
                .forEach { line ->
                    val part = line.split(";")  //Parse data from the CSV line
                    val id = part[0].toInt()
                    val habitats = part[1].map { Terrain.fromValue(it.toString()) }.toMutableList()
                    val wildlife = part[2].map { Animal.fromValue(it.toString()) }
                    val keystone = when (part[3].lowercase()) {
                        "yes" -> true
                        else -> false
                    }

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
                    if (startTileList.size == 3) {
                        startTiles.add(startTileList.toList())
                        startTileList.clear()
                    }
                }
        }
        return startTiles
    }

    /**
     *  End a turn of a [CascadiaGame] by refilling the shop
     *  and switching the [CascadiaGame.currentPlayer] to the next player in [CascadiaGame.playerList].
     *  Resolve all occurring overpopulations of four that appear during the shop refill.
     *  Call a GUI-refresh afterwards.
     *
     *  If the [CascadiaGame.habitatTileList] is empty prior to the shop refill
     *  [nextTurn] will end the game by calling a GUI-refresh.
     *
     *  @throws IllegalStateException if player has not yet added a tile to his habitat
     *
     */
    fun nextTurn() {

        // check prerequisites
        // check for existing game
        val game = rootService.currentGame
        checkNotNull(game)


        // check if player performed action
        check(game.hasPlayedTile) { "Player must at least add a habitat tile each turn" }

        // check for game end and if so, calculate score and update GUI
        if (game.habitatTileList.size == 0) {
            onAllRefreshables { refreshAfterGameEnd(rootService.scoringService.calculateScore()) }
            return
        }

        // refill shop
        val newHabitatTile = game.habitatTileList.removeLast()
        val newWildlifeToken = game.wildlifeTokenList.removeLast()
        for (i in 0 until game.shop.size) {
            // refill missing pair
            if (game.shop[i].first == null && game.shop[i].second == null) {
                game.shop[i] = Pair(newHabitatTile, newWildlifeToken)
            }
            // refill missing habitatTile from custom pair
            else if (game.shop[i].first == null) {
                game.shop[i] = Pair(newHabitatTile, game.shop[i].second)
            }
            // refill missing wildlifeToken from custom pair
            else if (game.shop[i].second == null) {
                game.shop[i] = Pair(game.shop[i].first, newWildlifeToken)
            }
        }

        // check for and resolve possible overpopulation of four
        if (checkForSameAnimal()) {
            resolveOverpopulation()
        }

        game.hasReplacedThreeToken = false
        game.hasPlayedTile = false

        // switch current player
        val nextPlayerIndex = (game.playerList.indexOf(game.currentPlayer) + 1) % game.playerList.size
        game.currentPlayer = game.playerList[nextPlayerIndex]

        // refresh GUI
        onAllRefreshables { refreshAfterNextTurn() }
    }

    /**
     * resolve an overpopulation of four by replacing all wildlife tokens in the games shop with new ones
     * from the wildlife token list.
     * If not enough tokens are available to replace the overpopulation, end the current game.
     * If resolution of the current overpopulation leads to another one, the latter will also either be resolved
     * or the game be ended.
     */
    fun resolveOverpopulation() {

        //check if game exists
        val game = rootService.currentGame
        checkNotNull(game)


        // if Tokenlist is empty or not enough tokens left to automatically resolve an overpopulation, end game
        if (game.wildlifeTokenList.size == 0 || game.wildlifeTokenList.size < 4) {
            onAllRefreshables { refreshAfterGameEnd(rootService.scoringService.calculateScore()) }
        }

        // perform actual token replacement
        executeTokenReplacement(listOf(0, 1, 2, 3))


    }

    /**
     * Check whether the token in the game [CascadiaGame.shop] at the given indices have the same animal value.
     *
     * @param tokenIndices is list of indices for wildLifeToken in the game shop. Default is list of all four indices.
     *
     * @return true if all token in shop at the indices in [tokenIndices] have the same animal value, false if not.
     *
     * @throws IllegalArgumentException if indices out of bound for shop, not mutually distinct
     * or number of indices is either too big or too small.
     *
     */
    fun checkForSameAnimal(tokenIndices: List<Int> = listOf(0, 1, 2, 3)): Boolean {

        // check for existing game
        val game = rootService.currentGame
        checkNotNull(game)

        // check if argument contains any indices
        require(tokenIndices.size in 1..4) { "number of indices must be between 0 and 5" }

        //check whether indices are not the same
        require(tokenIndices.distinct().size == tokenIndices.size) { "All indices must be different" }

        // check if indices in argument in range
        tokenIndices.forEach { require(it in 0..3) { "Indices for tokens must be between 0 and 3" } }

        // check for same animal in all tokens at given indices in game shop
        val firstToken = game.shop[tokenIndices[0]].second
        checkNotNull(firstToken)
        val firstAnimal = firstToken.animal
        for (index in tokenIndices) {
            val currentToken = game.shop[index].second
            checkNotNull(currentToken)
            if (currentToken.animal != firstAnimal) {
                return false
            }
        }
        return true
    }

    /**
     * Perform the actual replacement of tokens in the shop and trigger GUI update afterwards.
     * Usage of this function assumes that there either is an overpopulation of three the player can resolve
     * for free or that the player want's to replace an arbitrary number of tokens by using a nature token.
     * Used for this purposes in [PlayerActionService.replaceWildlifeTokens] and [resolveOverpopulation].
     *
     * @param [tokenIndices] is a list of indices of the tile-token pairs in [CascadiaGame.shop]
     * whose token shall be replaced.
     * @param networkReplacement is a boolean to flag replacements done by other network players.
     * @param natureTokenUsed is a boolean to flag the usage of nature token
     * as this requires different handling of discarded tokens.
     *
     */
    fun executeTokenReplacement(
        tokenIndices: List<Int>,
        networkReplacement: Boolean = false,
        natureTokenUsed: Boolean = false
    ) {
        val game = checkNotNull(rootService.currentGame)
        val myTurn1 = rootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN
        var myTurn2 = rootService.networkService.connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS

        // perform replacement
        tokenIndices.forEach {
            game.discardedToken.add(game.shop[it].second)
            game.shop[it] = Pair(
                game.shop[it].first,
                game.wildlifeTokenList.removeLast()
            )
        }
        if ((myTurn1 or myTurn2) && !natureTokenUsed) {
            rootService.networkService.sendResolvedOverPopulationMessage()
        }

        if (natureTokenUsed) {
            // return discarded wildlifeTokens
            game.discardedToken.forEach{
                checkNotNull(it)
                game.wildlifeTokenList.add(it)
            }
            game.discardedToken = mutableListOf()
            if (!networkReplacement) {
                game.wildlifeTokenList.shuffle()
                sendSwapMessage(myTurn1, tokenIndices)
            }

            // resolve possible overpopulation of four
            if (checkForSameAnimal() && !networkReplacement) {
                resolveOverpopulation()
            }
        } else {
            // resolve possible overpopulation of four
            if (checkForSameAnimal() && !networkReplacement) {
                resolveOverpopulation()
            }
            // return discarded wildlifeTokens
            else {
                game.discardedToken.forEach{
                    checkNotNull(it)
                    game.wildlifeTokenList.add(it)
                }
                game.discardedToken = mutableListOf()
                if (!networkReplacement) {
                    game.wildlifeTokenList.shuffle()
                    myTurn2 = rootService.networkService.connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS
                    sendShuffleMessage(myTurn2)
                }
            }
        }

        // refresh GUI Elements
        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }

    }

    /**
     * helperfunction to reduce complexity and nesting in exectueTokenReplacement.
     */
    private fun sendSwapMessage( condition : Boolean, tokenIndices: List<Int>) {
        if (condition) { rootService.networkService.sendSwappedWithNatureTokenMessage(tokenIndices) }
    }

    /**
     * helperfunction to reduce complexity and nesting in exectueTokenReplacement.
     */
    private fun sendShuffleMessage( condition : Boolean) {
        if (condition) { rootService.networkService.sendShuffledWildlifeTokensMessage() }
    }

    /**
     * Creates a List of all Coordinates where the current Player can place a tile
     *
     * @return List<Pair<Int,Int>>
     */
    fun getAllPossibleCoordinatesForTilePlacing(
        habitat: MutableMap<Pair<Int, Int>, HabitatTile>
    ): List<Pair<Int, Int>> {
        val coordinates = hashSetOf<Pair<Int, Int>>()

        habitat.forEach {
            val key = it.key
            if (habitat[Pair(key.first + 1, key.second - 1)] == null) coordinates.add(
                Pair(
                    key.first + 1,
                    key.second - 1
                )
            )
            if (habitat[Pair(key.first, key.second - 1)] == null) coordinates.add(
                Pair(
                    key.first,
                    key.second - 1
                )
            )
            if (habitat[Pair(key.first - 1, key.second)] == null) coordinates.add(
                Pair(
                    key.first - 1,
                    key.second
                )
            )
            if (habitat[Pair(key.first - 1, key.second + 1)] == null) coordinates.add(
                Pair(
                    key.first - 1,
                    key.second + 1
                )
            )
            if (habitat[Pair(key.first, key.second + 1)] == null) coordinates.add(
                Pair(
                    key.first,
                    key.second + 1
                )
            )
            if (habitat[Pair(key.first + 1, key.second)] == null) coordinates.add(
                Pair(
                    key.first + 1,
                    key.second
                )
            )
        }
        return coordinates.toList()
    }

    /**
     * Creates a List of all HabitatTiles, where the current Player can place the given Animal
     *
     * @param animal the animal Type
     * @return List<HabitatTile>
     */
    fun getAllPossibleTilesForWildlife(
        animal: Animal,
        habitat: MutableMap<Pair<Int, Int>, HabitatTile>
    ): List<HabitatTile> {
        return habitat.values.filter { it.wildlifeToken == null && it.wildlifeSymbols.contains(animal) }
    }
}