package service

import entity.PlayerType

/**
 * Service for an easy bot
 */
class EasyBotService(private val rootService: RootService) {
    val playerActionService = PlayerActionService(rootService)
    val gameService = GameService(rootService)

    //Chances for moves
    val PLACEWILDLIFECHANCE = 70
    val USENATURETOKENCHANCE = 10
    val RESOLVEOVERPOPULATIONCHANCE = 80

    /**
     * takes the Turn for am easy bot
     */
    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.currentPlayer

        var hasChosenCustomPair = false
        var hasResolvedOverpopulation = false

        /**
         * Resolves the overpopulation of 3 if it is possible and if the chance allows it.
         */
        fun resolveOverpopulation() {
            if (game.shop.groupBy { it.second }.values.any { it.size == 3 }
                && RESOLVEOVERPOPULATIONCHANCE <= (1..100).random()
                && !hasResolvedOverpopulation) {

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

        //Maybe uses wildlifetoken
        if (player.natureToken >= 1 && USENATURETOKENCHANCE <= (1..100).random()) {
            if ((1..2).random() == 1) {
                //replace WildelifeTokens

                val list = mutableListOf<Int>()
                for (i in 0..3) {
                    if ((1..3).random() == 1) {
                        list.add(i)
                    }
                }
                playerActionService.replaceWildlifeTokens(list)
            } else {
                //choose CustomPair

                var tile = 0
                var animal = 0

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
        val rotation = (0..6).random()
        for (i in 1..rotation) {
            playerActionService.rotateTile()
        }

        //place the tile
        playerActionService.addTileToHabitat(gameService.getAllPossibleCoordinatesForTilePlacing().random())

        //maybe places the wildlife
        if (PLACEWILDLIFECHANCE <= (1..100).random()) {
            val tiles = gameService.getAllPossibleTilesForWildlife(game.selectedToken.animal)
            if (tiles.isNotEmpty()) {
                playerActionService.addToken(game.selectedToken, tiles.random())
            } else {
                playerActionService.discardToken()
            }
        } else {
            playerActionService.discardToken()
        }
    }
}