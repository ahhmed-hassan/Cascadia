package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun startNewGame(playerNames: Map<String, PlayerType>, scoreRules: List<Boolean>) {
        //ToDo

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     *  End a turn of a [CascadiaGame] by refilling the shop
     *  and switching the [currentPlayer] to the next player in [PlayerList].
     *  Resolve all occurring overpopulations of four that appear during the shop refill.
     *  Call a GUI-refresh afterwards.
     *
     *  If the [habitatTileStack] is empty prior to the shop refill
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
        val newHabitatTile = game.habitatTileList[game.habitatTileList.size-1]
        val newWildlifeToken = game.wildlifeTokenList[game.wildlifeTokenList.size-1]
        for (i in 0 until game.shop.size) {
            // refill missing pair
            if(game.shop[i].first == null && game.shop[i].second == null) {
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
        if(checkForSameAnimal()) {
            resolveOverpopulation()
        }

        // switch current player
        val nextPlayerIndex = game.playerList.indexOf(game.currentPlayer)+1 % game.playerList.size
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
        executeTokenReplacement(listOf(0,1,2,3))


    }

    /**
     * Check whether the token in the game [shop] at the given indices have the same animal value.
     *
     * @param tokenIndices is list of indices for wildLifeToken in the game shop. Default is list of all four indices.
     *
     * @return [true] if all token in shop at the indices in [tokenIndices] have the same animal value, [false] if not.
     *
     * @throws IllegalArgumentException if indices out of bound for shop, not mutually distinct
     * or number of indices is either too big or too small.
     *
     */
    fun checkForSameAnimal( tokenIndices : List<Int> = listOf(0,1,2,3) ): Boolean {

        // check for existing game
        val game = rootService.currentGame
        checkNotNull(game)

        // check if argument contains any indices
        require(tokenIndices.size in 1..4) {"number of indices must be between 0 and 5"}

        //check whether indices are not the same
        require(tokenIndices.distinct().size == tokenIndices.size) {"All indices must be different"}

        // check if indices in argument in range
        tokenIndices.forEach { require(it in 0 .. 3) {"Indices for tokens must be between 0 and 3"} }

        // check for same animal in all tokens at given indices in game shop
        val firstToken = game.shop[tokenIndices[0]].second
        checkNotNull(firstToken)
        val firstAnimal = firstToken.animal
        for (i in 1..3) {
            val currentToken = game.shop[tokenIndices[i]].second
            checkNotNull(currentToken)
            if(currentToken.animal != firstAnimal) {
                return false
            }
        }
        return true
    }

    /**
     * Perform the actual replacement of tokens in the shop and trigger GUI update afterwards.
     * Usage of this function assumes that there either is an overpopulation of three the player can resolve
     * for free or that the player want's to replace an arbitrary number of tokens by using a nature token.
     * Used for this purposes in [replaceWildlifeTokens] and [resolveOverpopulation].
     *
     * @param [tokenIndices] is a list of indices of the tile-token pairs in [shop] whose token shall be replaced.
     * @param networkReplacement is a boolean to flag replacements done by other network players.
     *
     */
    fun executeTokenReplacement(tokenIndices : List<Int>, networkReplacement : Boolean = false) {

        //check for existing game
        val game = checkNotNull(rootService.currentGame)

        // perform replacement
        tokenIndices.forEach {
            game.discardedToken.add(game.shop[it].second)
            game.shop[it] = Pair(game.shop[it].first,
                game.wildlifeTokenList.removeLast())
        }

        // resolve possible overpopulation of four
        if (checkForSameAnimal() && !networkReplacement) {
            resolveOverpopulation()
        }

        // return discarded wildlifeTokens
        else {
            for (token in game.discardedToken) {
                checkNotNull(token)
                game.wildlifeTokenList.add(token)
            }
            game.discardedToken = mutableListOf()
            if (!networkReplacement) {
                game.wildlifeTokenList.shuffle()
            }

        }

        // refresh GUI Elements
        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }

    }

    /**
     * Creates a List of all Coordinates where the current Player can place a tile
     *
     * @return List<Pair<Int,Int>>
     */
    fun getAllPossibleCoordinatesForTilePlacing(): List<Pair<Int, Int>> {
        val coordinates = hashSetOf<Pair<Int, Int>>()
        val habitat = rootService.currentGame?.currentPlayer?.habitat
        checkNotNull(habitat)

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
    fun getAllPossibleTilesForWildlife(animal: Animal): List<HabitatTile> {
        val habitat = rootService.currentGame?.currentPlayer?.habitat
        checkNotNull(habitat)

        return habitat.values.filter { it.wildlifeToken == null && it.wildlifeSymbols.contains(animal) }
    }
}