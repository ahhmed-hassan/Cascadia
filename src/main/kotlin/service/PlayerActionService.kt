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
     * Choose a custom pair of [HabitatTile] and [WildlifeToken] from the tile-token-pairs in [CascadiaGame.shop].
     * Chosen tile is saved as selectedTile and chosen token as selectedToken.
     * Their former locations in the shop pairs are set to [null].
     * Decrease [Player]s natureToken by one afterwards.
     *
     * @param tileIndex is the index of the pair in the shop whose [HabitatTile] is chosen
     * @param tokenIndex is the index of the pair in the shop whose [WildlifeToken] is chosen
     *
     * @throws IllegalArgumentException if one of the arguments is smaller than 0 or greater than 3
     * @throws IllegalStateException if player is not allowed to choose a pair
     * or if player does not have enough nature tokens to choose a custom pair
     *
     */
    fun chooseCustomPair(tileIndex : Int, tokenIndex : Int) {
        // check prerequisites
        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game)

        // check if player allowed to choose tile
        if (game.selectedTile != null || game.selectedToken != null || game.hasPlayedTile) {
            throw IllegalStateException("Player already selected a pair")
        }
        if (game.currentPlayer.natureToken < 1) {
            throw IllegalStateException("Player has no nature token left to select custom pair")
        }

        // check arguments
        if (tileIndex < 0 || tileIndex > 3 || tokenIndex < 0 || tokenIndex > 3) {
            throw IllegalArgumentException("Index for both tile and token must be between 0 and 3")
        }

        // select custom pair
        game.selectedTile = game.shop[tileIndex].first
        game.selectedToken = game.shop[tokenIndex].second

        // remove chosen elements from shop
        game.shop[tileIndex] = Pair(null, game.shop[tileIndex].second)
        game.shop[tokenIndex] = Pair(game.shop[tokenIndex].first, null)

        // decrease players nature token
        game.currentPlayer.natureToken--

        // refresh GUI
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