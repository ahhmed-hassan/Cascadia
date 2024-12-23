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
        check(game.selectedTile != null || game.selectedToken != null || game.hasPlayedTile, ) {
            "Player already selected a pair"
        }
        check(game.currentPlayer.natureToken >= 1) {"Player has no nature token left to select custom pair"}

        // check arguments
        require(tileIndex in 0..3) {"Index for tile must be between 0 and 3"}
        require(tileIndex in 0..3) {"Index for token must be between 0 and 3"}

        // select custom pair
        game.selectedTile = game.shop[tileIndex].first
        game.selectedToken = game.shop[tokenIndex].second

        // remove chosen elements from shop
        game.shop[tileIndex] = Pair(null, game.shop[tileIndex].second)
        game.shop[tokenIndex] = Pair(game.shop[tokenIndex].first, null)

        // decrease players nature token
        game.currentPlayer.natureToken--

        // refresh GUI
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
     * @param tile The habitat tile where the wildlife token is to be placed.
     * @throws IllegalStateException If the tile already has a wildlife token.
     * @throws IllegalArgumentException If the wildlife token is not valid for the tile.
     */
    fun addToken(tile : HabitatTile) {
        val game = rootService.currentGame
        checkNotNull(game)
        val selectedToken = game.selectedToken
        checkNotNull(selectedToken)
        val currentPlayer = game.currentPlayer

        //Check if a wildlife token is already placed on this tile
        requireNotNull(tile.wildlifeToken){"There is already a wildlife token on this tile!"}

        //Check if the wildlife token is a valid token to begin with
        require(tile.wildlifeSymbols.contains(selectedToken.animal)){"Wildlife token cannot be placed on this tile!"}

        tile.wildlifeToken = selectedToken

        if(tile.isKeystoneTile){
            currentPlayer.natureToken += 1
        }

        game.selectedToken = null

        onAllRefreshables { refreshAfterWildlifeTokenAdded() }

        rootService.gameService.nextTurn()

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