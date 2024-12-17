package service

import entity.PlayerType

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService : RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun startNewGame(playerNames : Map<String,PlayerType>, scoreRules : List<Boolean>) {
        //ToDo

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     *
     */
    fun nextTurn() {
        //ToDo

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
        if (game.wildlifeTokenList.size == 0 || (game.wildlifeTokenList.size < 4)) {
            onAllRefreshables { refreshAfterGameEnd() }
        }

        // perform token replacement
        (0..3).forEach {
            game.discardedToken.add(game.shop[it].second)
            game.shop[it] = Pair(game.shop[it].first, game.wildlifeTokenList[game.wildlifeTokenList.size-1])
        }

        // resolve possible overpopulation of four
        if (checkForSameAnimal()) {
            resolveOverpopulation()
        }
        // return discarded wildlifeTokens
        else {
            for (token in game.discardedToken) {
                checkNotNull(token)
                game.wildlifeTokenList.add(token)
            }
            game.discardedToken = mutableListOf()
            game.wildlifeTokenList.shuffle()
        }

        // refresh GUI Elements
        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }
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
    internal fun checkForSameAnimal( tokenIndices : List<Int> = listOf(0,1,2,3) ): Boolean {

        // check for existing game
        val game = rootService.currentGame
        checkNotNull(game)

        // check if argument contains any indices
        require(tokenIndices.size > 0 || tokenIndices.size < 5) {"number of indices must be between 0 and 5"}

        //check whether indices are not the same
        require(tokenIndices.distinct().size == tokenIndices.size) {"All indices must be different"}

        // check if indices in argument in range
        tokenIndices.forEach { require(it > 3 || it < 0) {"Indices for tokens must be between 0 and 3"} }

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

}