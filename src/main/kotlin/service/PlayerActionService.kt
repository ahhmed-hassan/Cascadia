package service

import entity.WildlifeToken
import entity.HabitatTile

/**
 *  Service class for all actions that can be initialized by the player.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class PlayerActionService(private val rootSerivce : RootService) : AbstractRefreshingService() {

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
     * [discardToken] discards the currently selected token.
     */
    fun discardToken() {
        val game = rootSerivce.currentGame
        checkNotNull(game)

        game.selectedToken = null
        rootSerivce.gameService.nextTurn()
        onAllRefreshables { /*ToDo refreshAfterTokenDiscarded() */ }
    }


}