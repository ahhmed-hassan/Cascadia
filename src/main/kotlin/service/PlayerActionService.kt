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
     * [chooseTokenTilePair] is responsible for marking the chosen Token-Tile Pair as selected for further use.
     *
     * @param chosenPair This the pair that the player chose from the shop.
     */
    fun chooseTokenTilePair(chosenPair : Int) {
        val game = rootSerivce.currentGame
        checkNotNull(game)
        val shopTile = game.shop[chosenPair].first
        val shopToken = game.shop[chosenPair].second
        checkNotNull(shopTile)
        checkNotNull(shopToken)

        //mark the chosen Token-Tile Pair as selected
        game.selectedTile = shopTile
        game.selectedToken = shopToken

        //Delete pair out of shop by assigning a null pair
        game.shop[chosenPair] = Pair(null, null)

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
     *
     */
    fun discardToken() {
        //ToDo

        onAllRefreshables { refreshAfterNextTurn() }
    }


}