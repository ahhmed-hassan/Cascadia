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
     *Rotate the selected tile
     * preconditions : There is already a selected tile that has not been placed yet!
     * @throws IllegalArgumentException if there is no [HabitatTile] to place
     * post :
     * The [HabitatTile.rotationOffset] is incremented.
     * the [HabitatTile.terrains] would have the right order as how it would be placed.
     *
     */
    fun rotateTile() {
        val game = rootSerivce.currentGame
        requireNotNull(game){"No game started yet"}

        requireNotNull(game.selectedTile){"Only the selected tile can be rotated!"}

        game.selectedTile.rotationOffset = (game.selectedTile.rotationOffset+1).mod(game.selectedTile.terrains.size)
        game.selectedTile.terrains.add(0,
            game.selectedTile.terrains.removeAt(game.selectedTile.terrains.size-1))

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