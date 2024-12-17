package service

import entity.WildlifeToken
import entity.HabitatTile

/**
 *  Service class for all actions that can be initialized by the player.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class PlayerActionService(private val rootService : RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun chooseTokenTilePair(choosenPair : Int) {
        //ToDo

        onAllRefreshables { refreshAfterTokenTilePairChoosen() }
    }

    /**
     *
     */
    fun chooseCustomPair(titleIndex : Int, tokenIndex : Int) {
        //ToDo

        onAllRefreshables { refreshAfterTokenTilePairChoosen() }
    }

    /**
     * Replace a number of [WildlifeToken] from the tile-token pairs in the [shop] with new ones from [WildlifeToken].
     *
     * Is used in to replace an overpopulation of four equal tokens if it appears during [next Turn]
     * or player induced token replacements.
     *
     * Can be used for the free replacement of three token if three tokens in the [shop] are the same at the turn start.
     *
     * Can be used to replace a chosen number of tokens if the player has at least one nature token.
     * After that the number of nature tokens of the current player is reduced by one.
     *
     * If used during [nextTurn] to resolve an overpopulation of four the replaced tokens are stored in [discardedToken]
     * till all occouring overpopulations are resolved.
     *
     * If used during a turn, the replaced tokens are stored in [discardedToken] till the end of the current turn
     * and are added back to [WildlifeToken] afterwards in [nextTurn].
     *
     * @param [tokenIndices] is a list of indices of the tile-token pairs in [shop] whose token shall be replaced
     * @param [playerAction] indicates whether the replacement was issued by the player or not.
     * [true] if action of player. [false] if used during [nextTurn]. Default is [true].
     *
     * @throws illegalArgumentException if [tokenIndices] contains an [Int] n with n < 0 or n > 3
     * @throws illegalStateException if [hasReplacedThreeToken] is True and [CurrentPlayer]s [natureToken] is zero
     * @throws illegalStateException if [hasReplacedThreeToken] is False and [CurrentPlayer]s [natureToken] is zero
     * and [WildlifeToken] in [shop] at indices in [tokenIndices] don't have the same value
     *
     */
    fun replaceWildlifeTokens(tokenIndices : List<Int>, playerAction : Boolean = true) {

        //check if game exists
        val game = rootService.currentGame
        checkNotNull(game)

        // check if indices in argument in range
        for (index in tokenIndices) {
            if (index > 3 || index < 0) {
                throw IllegalArgumentException("Indices for tokens must be between 0 and 3")
            }
        }

        // check if player is allowed to perform action
        if (game.hasReplacedThreeToken && game.currentPlayer.natureToken == 0) {
            throw IllegalStateException("Current Player not allowed to perform replacement")
        }

        // check if enough tokens are left in wildLifeTokenList for replacement
        // if list is empty or not enough tokens left to automatically resolve an overpoulation of four
        // end game
        if (game.wildlifeTokenList.size == 0 || (game.wildlifeTokenList.size < tokenIndices.size && !playerAction)) {
            onAllRefreshables { refrefhAfterGameEnd() }
        }
        // if not enough tokens left to perform a player replacement, the player can try again with a smaller amount
        else if (game.wildlifeTokenList.size < tokenIndices.size && playerAction) {
            throw IllegalStateException( "Not enough wildlifeTokens for replacement left. " +
                                         "Replacement of ${game.wildlifeTokenList.size} Tokens still possible.")
        }

        // check if replacement is legitimate
        // replacement of four wildlifeToken
        if (tokenIndices.size == 4) {
            // check if action is done by player or during nextTurn to resolve overpopulation of four
            // if done by player, player must have at least one nature token
            if (playerAction && game.currentPlayer.natureToken <= 0) {
                throw IllegalStateException("Current Player not allowed to perform replacement")
            }
            else if (game.currentPlayer.natureToken > 0) {
                game.currentPlayer.natureToken--
            }
            // check if token at indices have same animal if used to resolve overpopulation of four in nextTurn
            else{
                val currentAnimal = game.shop[tokenIndices[0]].second.animal
                for (index in tokenIndices) {
                    if (game.shop[tokenIndices[index]].second.animal != currentAnimal) {
                        throw IllegalArgumentException("Token on indices do not have the same Animal")
                    }
                }
            }

        }
        // replacement of three wildlifeToken
        else if (tokenIndices.size == 3) {
            // check if player is allowed to perform action
            // player is allowed to perform action if doing a free resolve of an overpopulation of three
            // or by using nature token
            if (game.hasReplacedThreeToken && game.currentPlayer.natureToken == 0) {
                throw IllegalStateException("Current Player not allowed to perform replacement")
            } else if (!game.hasReplacedThreeToken) {
                // for free replacement of overpopulation of three, tokens need to have equal animal
                val currentAnimal = game.shop[tokenIndices[0]].second.animal
                for (index in tokenIndices) {
                    if (game.shop[tokenIndices[index]].second.animal != currentAnimal) {
                        throw IllegalArgumentException("Token on indices do not have the same Animal")
                    }
                }

                game.hasReplacedThreeToken = true
            } else {
                game.currentPlayer.natureToken--
            }
        }
        // replacement of two or one wildlifeToken
        else {
            // check if player is allowed to perform action by having at least one nature token
            if (game.currentPlayer.natureToken <= 0) {
                throw IllegalStateException("Current Player not allowed to perform replacement")
            } else {
                game.currentPlayer.natureToken--
            }
        }

        // perform actual replacement
        for (index in tokenIndices) {
            game.discardedToken.add(game.shop[index].second)
            game.shop[index] = Pair(game.shop[index].first, game.wildlifeTokenList[game.wildlifeTokenList.size-1])
        }

        // check if replacement created an overpopulation of four
        var overpopulation = true
        val currentAnimal = game.shop[tokenIndices[0]].second.animal
        for (index in 1..3) {
            if (game.shop[tokenIndices[index]].second.animal != currentAnimal) {
                overpopulation = false
                break
            }
        }
        // resolve possible overpopulation of four
        if (overpopulation) {
            replaceWildlifeTokens(listOf(1,2,3,4), false)
        }

        // return discarded wildlifeTokens
        for (token in game.discardedToken) {
            game.wildlifeTokenList.add(token)
        }
        game.wildlifeTokenList.shuffle()

        // refresh GUI Elements
        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }
    }

    /**
     *
     */
    fun addTileToHabitat(habitatCoordinates : Pair<Int, Int>) {
        //ToDo

        onAllRefreshables { refreshAfterHabitatTileAdded() }
    }

    /**
     *
     */
    fun addToken(token: WildlifeToken, tile : HabitatTile) {
        //ToDo

        onAllRefreshables { refreshAfterWildlifeTokenAdded() }
    }

    /**
     *
     */
    fun rotateTile(tile: HabitatTile) {
        //ToDo

        onAllRefreshables { refreshAfterTileRotation() }
    }

    /**
     *
     */
    fun discardToken() {
        //ToDo

        onAllRefreshables { /*ToDo refreshAfterTokenDiscarded() */ }
    }


}