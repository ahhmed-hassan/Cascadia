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
     * [chooseTokenTilePair] This function is responsible for saving a Token-Tile pair as "selected" within the game.
     * Additionally, after a pair is selected, it is replaced in the shop with a placeholder pair of null values
     *
     * @param chosenPair These are the indices of the pair that the player chose from the shop.
     *
     * @throws IllegalArgumentException if the chosenPair is out of bounds
     */
    fun chooseTokenTilePair(chosenPair : Int) {
        val game = rootService.currentGame
        checkNotNull(game)

        // check if chosenPair is not out of bounds
        require(chosenPair in 0..3) {"Index for pair must be between 0 and 3"}

        val shopTile = game.shop[chosenPair].first
        val shopToken = game.shop[chosenPair].second
        checkNotNull(shopTile)
        checkNotNull(shopToken)

        require(game.selectedTile == null && game.selectedToken == null)

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