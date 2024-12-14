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
     * [chooseTokenTilePair] is responsible for marking the chosen Token-Tile Pair as selected for further use.
     *
     * @param choosenPair This the pair that the player chose from the shop.
     */
    fun chooseTokenTilePair(choosenPair : Int) {
        val game = rootSerivce.currentGame
        checkNotNull(game)

        game.selectedTile = game.shop[choosenPair].first
        game.selectedToken = game.shop[choosenPair].second

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
     *
     */
    fun discardToken() {
        //ToDo

        onAllRefreshables { /*ToDo refreshAfterTokenDiscarded() */ }
    }


}