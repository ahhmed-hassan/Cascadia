package service

import entity.PlayerType

/**
 * Service for an easy bot
 */
class EasyBotService(private val rootService: RootService) {
    val playerActionService = PlayerActionService(rootService)
    val gameService = GameService(rootService)

    //Chances for moves
    val placeWildlifeChance = 70
    val useNatureTokenChance = 10
    val resolveOverpopulationChance = 80
    val replaceSingleTokenChance = 33

    /**
     * takes the Turn for am easy bot
     */
    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.currentPlayer

        var hasChosenCustomPair = false

        /**
         * Resolves the overpopulation of 3 if it is possible and if the chance allows it.
         */
        fun resolveOverpopulation() {
            if (game.shop.groupBy { it.second }.values.any { it.size == 3 }
                && resolveOverpopulationChance <= (1..100).random()
                && !game.hasReplacedThreeToken) {

                val animal = game.shop.groupBy { it.second }.values.first { it.size == 3 }[0].second
                val indices = mutableListOf<Int>()
                game.shop.forEachIndexed { index, it -> if (it.second == animal) indices.add(index) }
                playerActionService.replaceWildlifeTokens(indices)
            }
        }

        assert(player.playerType == PlayerType.EASY) {
            "PlayerType must be easy bot"
        }

        resolveOverpopulation()

        //Maybe uses natureToken
        if (player.natureToken >= 1 && useNatureTokenChance <= (1..100).random()) {
            if ((1..2).random() == 1) {
                //replace WildelifeTokens

                val list = mutableListOf<Int>()
                for (i in 0..3) {
                    if (replaceSingleTokenChance <= (1..100).random()) {
                        list.add(i)
                    }
                }
                playerActionService.replaceWildlifeTokens(list)
            } else {
                //choose CustomPair

                var tile : Int
                var animal : Int

                do {
                    tile = (0..3).random()
                    animal = (0..3).random()
                } while (tile == animal)

                playerActionService.chooseCustomPair(tile, animal)
                hasChosenCustomPair = true
            }
        }

        resolveOverpopulation()

        //chooses a pair if it has not happened yet
        if (!hasChosenCustomPair) {
            playerActionService.chooseTokenTilePair((0..3).random())
        }

        //maybe rotates the tile before placing
        val rotation = (0..5).random()
        for (i in 1..rotation) {
            playerActionService.rotateTile()
        }

        //place the tile
        playerActionService.addTileToHabitat(
            gameService.getAllPossibleCoordinatesForTilePlacing(player.habitat).random()
        )

        //maybe places the wildlife
        if (placeWildlifeChance <= (1..100).random()) {
            val selectedToken = game.selectedToken
            checkNotNull(selectedToken)
            val tiles = gameService.getAllPossibleTilesForWildlife(selectedToken.animal, player.habitat)
            if (tiles.isNotEmpty()) {
                playerActionService.addToken(tiles.random())
            } else {
                playerActionService.discardToken()
            }
        } else {
            playerActionService.discardToken()
        }
    }
}