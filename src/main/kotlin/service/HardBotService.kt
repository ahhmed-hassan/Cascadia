package service

import entity.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.LinkedBlockingQueue

class HardBotService(private val rootService: RootService) {
    val gameService = GameService(rootService)

    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        require(game.currentPlayer.playerType == PlayerType.NORMAL)

        Thread {
            takeAsyncTurn(game)
        }.start()
    }

    private fun takeAsyncTurn(game: CascadiaGame) = runBlocking {

        val tiles = game.habitatTileList.shuffled()
        val animalTokens = game.wildlifeTokenList.shuffled()
        val numberOfPlayers = game.playerList.size
        val currendRound =
            if (tiles.size % numberOfPlayers == 0) tiles.size / numberOfPlayers + 1 else tiles.size / numberOfPlayers + 2

        val queue = LinkedBlockingQueue<HardBotJob>()
        var timeIsUp = false

        launch {
            delay(8000)
            //Todo: start first calcualtion
            delay(1000)
            timeIsUp = true
            queue.clear()
            delay(50)
            //Todo: start second calcualtion
            delay(500)
            //TODO: TakeRealTurn()
        }

        val maxNumberOfThreads = Runtime.getRuntime().availableProcessors()

        val posibillities = calculateAllPossibilities(tiles, game)
    }

    private fun createJob(
        tiles: List<HabitatTile>,
        player: Player,
        round: Int,
        animals: MutableList<WildlifeToken>,
        queue: LinkedBlockingQueue<HardBotJob>,
        employer: HardBotPossiblePlacements,
        shop: MutableList<Pair<HabitatTile?, WildlifeToken?>>,
        tile: HabitatTile
    ) {
        val habitat = player.habitat.mapValues { entry -> entry.value.copy() }.toMutableMap()
        val naturalTokens = if (employer.usedNaturalToken) player.natureToken - 1 else player.natureToken

        val replacedWildlife = employer.replacedWildlife
        val wildlifeToken = employer.wildlifeToken
        if (replacedWildlife != null && wildlifeToken != null) {
            val index = shop.indexOfFirst { it.first == tile } //TODO: find out it it needs a -1
            if (replacedWildlife[index]) {
                shop[index].second?.let { animals.add(it) }
                shop[index] = Pair(shop[index].first, wildlifeToken)
                animals.remove(wildlifeToken)
            }

            for (i in replacedWildlife.indices) {
                if (replacedWildlife[i] && i != index) {
                    shop[i].second?.let { animals.add(animals.size - 1, it) }
                    shop[i] = Pair(shop[i].first, animals.removeFirst())
                }
            }
        } else {
            if (replacedWildlife != null) {
                for (i in replacedWildlife.indices) {
                    if (replacedWildlife[i]) {
                        shop[i].second?.let { animals.add(animals.size - 1, it) }
                        shop[i] = Pair(shop[i].first, animals.removeFirst())
                    }
                }
            }
        }

        val customPair = employer.customPair
        if (customPair != null) {

        }
    }

    private fun calculateAllPossibilities(
        tiles: List<HabitatTile>,
        game: CascadiaGame
    ): MutableList<HardBotPossiblePlacements> {
        val shop = game.shop
        val player = game.currentPlayer
        val possibleTilePlaces = gameService.getAllPossibleCoordinatesForTilePlacing(player.habitat)

        val list = LinkedBlockingQueue<HardBotPossiblePlacements>()
        val threads = mutableListOf<Thread>()

        threads.add(Thread {
            val habitat = player.habitat.mapValues { entry -> entry.value.copy() }.toMutableMap()
            shop.forEach { pair ->
                val first = pair.first
                val second = pair.second
                checkNotNull(first)
                checkNotNull(second)
                possibleTilePlaces.forEach { tilePlace ->
                    habitat[tilePlace] = first
                    val animalPositions = gameService.getAllPossibleTilesForWildlife(second.animal, habitat)
                    animalPositions.forEach { animalPlace ->
                        for (rotation in 0..5) {
                            list.add(
                                HardBotPossiblePlacements(
                                    tile = first,
                                    tilePlacement = tilePlace,
                                    rotation = rotation,
                                    wildlifeToken = second,
                                    usedNaturalToken = false,
                                    wildlifePlacement = animalPlace,
                                )
                            )
                        }
                    }
                    for (rotation in 0..5) {
                        list.add(
                            HardBotPossiblePlacements(
                                tile = first,
                                tilePlacement = tilePlace,
                                rotation = rotation,
                                usedNaturalToken = false,
                            )
                        )
                    }
                    habitat.remove(tilePlace)
                }
            }
        })

        for (thread in threads) {
            thread.start()
        }

        for (thread in threads) {
            thread.join()
        }

        return list.toMutableList()
    }
}