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

        onAllRefreshables { refreshAfterTokenTilePairChosen() }
    }

    /**
     *
     */
    fun chooseCustomPair(titleIndex : Int, tokenIndex : Int) {
        //ToDo

        onAllRefreshables { refreshAfterTokenTilePairChosen() }
    }

    /**
     *
     */
    fun replaceWildlifeTokens(tokenIndices : List<Int>) {
        //ToDo

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
     * [discardToken] discards the currently selected token and adds it back to the wildlife token bag.
     *
     * @throws IllegalStateException if the selected Token is null
     */
    fun discardToken() {
        val game = rootService.currentGame
        checkNotNull(game)
        val selectedToken = game.selectedToken
        checkNotNull(selectedToken)

        game.wildlifeTokenList.add(selectedToken)
        game.wildlifeTokenList.shuffle()
        game.selectedToken = null

        rootService.gameService.nextTurn()
    }


}