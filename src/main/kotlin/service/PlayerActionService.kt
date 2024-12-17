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
     * [addToken] is responsible for placing tokens on already placed habitat tiles.
     *
     * @param token The wildlife token to be placed on the habitat tile.
     * @param tile The habitat tile where the wildlife token is to be placed.
     * @throws IllegalStateException If the tile already has a wildlife token.
     * @throws IllegalArgumentException If the wildlife token is not valid for the tile.
     */
    fun addToken(token: WildlifeToken, tile : HabitatTile) {
        val game = rootSerivce.currentGame
        checkNotNull(game)
        val currentPlayer = game.currentPlayer

        //Check if a wildlife token is already placed on this tile
        if (tile.wildlifeToken != null) {
            throw IllegalStateException("There is already a wildlife token on this tile!")
        }

        //Check if the wildlife token is a valid token to begin with
        if(tile.wildlifeSymbols.contains(token.animal)){
            tile.wildlifeToken = token
            if(tile.isKeystoneTile){
                currentPlayer.natureToken += 1
            }
            game.selectedToken = null
            rootSerivce.gameService.nextTurn()
        }
        else{
            throw IllegalArgumentException("Wildlife token cannot be placed on this tile!")
        }

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