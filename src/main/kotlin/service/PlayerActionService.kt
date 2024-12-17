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
     * The method adds the current selected [WildlifeToken] to the given coordinates
     * @param habitatCoordinates the given coordinates of the [entity.Player.habitat]
     * @throws IllegalStateException if [entity.CascadiaGame.selectedTile] is null
     * @throws IllegalArgumentException if the parameters are not next to some already put [entity.HabitatTile]
     * After this function the [entity.CascadiaGame.selectedTile] is set to null
     */
    fun addTileToHabitat(habitatCoordinates : Pair<Int, Int>) {
        //ToDo
        val offsets = listOf(Pair(-1,1), Pair(0,1), Pair(1,0), Pair(1,-1), Pair(0,-1), Pair(-1,0))
        val possibleNeighbours = offsets.map {
            habitatCoordinates.first+it.first to habitatCoordinates.second + it.second }

        val game = checkNotNull(rootService.currentGame) {"No game started"}
        val selectedTile = checkNotNull(game.selectedTile){"No habitat tile has been chosen yet"}
        require(possibleNeighbours.any { game.currentPlayer.habitat.containsKey(it) }
        ){"A habitat tile shall only be placed to an already placed one"}
        game.currentPlayer.habitat[habitatCoordinates] = selectedTile
        game.selectedTile = null
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