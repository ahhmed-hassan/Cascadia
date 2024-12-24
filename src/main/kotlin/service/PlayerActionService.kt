package service

import entity.HabitatTile
import entity.WildlifeToken

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
    fun chooseCustomPair(tileIndex: Int, tokenIndex: Int) {
        // check prerequisites
        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game)

        // check if player allowed to choose tile
        check(game.selectedTile != null || game.selectedToken != null || game.hasPlayedTile) {
            "Player already selected a pair"
        }
        check(game.currentPlayer.natureToken >= 1) { "Player has no nature token left to select custom pair" }

        // check arguments
        require(tileIndex in 0..3) { "Index for tile must be between 0 and 3" }
        require(tileIndex in 0..3) { "Index for token must be between 0 and 3" }

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
    fun replaceWildlifeTokens(tokenIndices: List<Int>) {
        //ToDo

        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }
    }

    /**
     * The method adds the current selected [HabitatTile] to the given coordinates
     * @param habitatCoordinates the given coordinates of the [entity.Player.habitat]
     * @throws IllegalStateException if [entity.CascadiaGame.selectedTile] is null
     * @throws IllegalArgumentException if the parameters are not next to some already put [entity.HabitatTile]
     * After this function the [entity.CascadiaGame.selectedTile] is set to null
     */
    fun addTileToHabitat(habitatCoordinates: Pair<Int, Int>) {
        val offsets = listOf(Pair(-1, 1), Pair(0, 1), Pair(1, 0), Pair(1, -1), Pair(0, -1), Pair(-1, 0))
        val possibleNeighbours = offsets.map {
            habitatCoordinates.first + it.first to habitatCoordinates.second + it.second
        }

        val game = checkNotNull(rootService.currentGame) { "No game started" }
        require(!game.currentPlayer.habitat.containsKey(habitatCoordinates)) {
            "At this coordinate there is already an existing tile"
        }
        val selectedTile = checkNotNull(game.selectedTile) { "No habitat tile has been chosen yet" }
        require(possibleNeighbours.any { game.currentPlayer.habitat.containsKey(it) }
        ) { "A habitat tile shall only be placed to an already placed one" }
        game.currentPlayer.habitat[habitatCoordinates] = selectedTile
        game.selectedTile = null
        onAllRefreshables { refreshAfterHabitatTileAdded() }
    }

    /**
     * [addToken] is responsible for placing tokens on already placed habitat tiles.
     *
     * @param tile The habitat tile where the wildlife token is to be placed.
     * @throws IllegalStateException If the tile already has a wildlife token.
     * @throws IllegalArgumentException If the wildlife token is not valid for the tile.
     */
    fun addToken(tile: HabitatTile) {
        val game = rootService.currentGame
        checkNotNull(game)
        val selectedToken = game.selectedToken
        checkNotNull(selectedToken)
        val currentPlayer = game.currentPlayer

        //Check if a wildlife token is already placed on this tile
        requireNotNull(tile.wildlifeToken) { "There is already a wildlife token on this tile!" }

        //Check if the wildlife token is a valid token to begin with
        require(tile.wildlifeSymbols.contains(selectedToken.animal)) { "Wildlife token cannot be placed on this tile!" }

        tile.wildlifeToken = selectedToken

        if (tile.isKeystoneTile) {
            currentPlayer.natureToken += 1
        }

        game.selectedToken = null

        onAllRefreshables { refreshAfterWildlifeTokenAdded() }
    }

    /**
     * Rotate the selected tile
     * preconditions : There is already a selected tile that has not been placed yet!
     * @throws IllegalArgumentException if there is no [HabitatTile] to place
     * post :
     * The [HabitatTile.rotationOffset] is incremented.
     * the [HabitatTile.terrains] would have the right order as how it would be placed (one step clockwise rotated)
     *
     */
    fun rotateTile() {
        val game = checkNotNull(rootService.currentGame) { "No game started yet" }

        val selectedTile = checkNotNull(game.selectedTile) { "Only the selected tile can be rotated!" }

        selectedTile.rotationOffset = (selectedTile.rotationOffset + 1).mod(selectedTile.terrains.size)
        selectedTile.terrains.add(
            0, selectedTile.terrains.removeLast()
        )

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