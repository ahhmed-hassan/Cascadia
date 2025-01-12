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
        val animalTokens = game.wildlifeTokenList.shuffled().toMutableList()
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

        val posibillities = calculateAllPossibilities(tiles, game, animalTokens)
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
        val naturalTokens = if (employer.usedNaturalToken > 0) player.natureToken - 1 else player.natureToken

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
        game: CascadiaGame,
        tokenList: MutableList<WildlifeToken>
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
                                    usedNaturalToken = 0,
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
                                usedNaturalToken = 0,
                            )
                        )
                    }
                    habitat.remove(tilePlace)
                }
            }
        })

        if (player.natureToken > 0) {
            threads.add(Thread {
                val habitat = player.habitat.mapValues { entry -> entry.value.copy() }.toMutableMap()

                var selectedToken: WildlifeToken? = null
                var selectedTokenIndex = 0
                for (animal in Animal.values()) {
                    var tokenChance: Number?
                    if (animal in shop.map {
                            val token = checkNotNull(it.second)
                            token.animal
                        }) {

                        tokenChance = 1

                        val firstPair = shop.find { checkNotNull(it.second).animal == animal }

                        selectedToken = checkNotNull(firstPair).second
                        selectedTokenIndex = shop.indexOf(firstPair)
                    } else if (player.natureToken > 1) {
                        val k = tokenList.filter {
                            it.animal == animal
                        }.size
                        if (k == 0) continue
                        val n = tokenList.size
                        val tokenChanceComplement = 0L +
                                ((n - k) * (n - k - 1) * (n - k - 2) * (n - k - 3)) / (n * (n - 1) * (n - 2) * (n - 3))
                        tokenChance = 1 - tokenChanceComplement

                        val replacedToken = tokenList.first { it.animal == animal }
                        tokenList.remove(replacedToken)
                        shop[0] = Pair(game.shop[0].first, replacedToken)

                        selectedToken = shop[0].second
                    } else {
                        continue
                    }

                    for (i in 0..3) {
                        if (i == selectedTokenIndex) {
                            continue
                        }
                        val selectedTile = shop[i].first
                        checkNotNull(selectedTile)

                        possibleTilePlaces.forEach { tilePlace ->
                            habitat[tilePlace] = selectedTile
                            val animalPositions = gameService.getAllPossibleTilesForWildlife(
                                checkNotNull(selectedToken).animal, habitat
                            )
                            animalPositions.forEach { animalPlace ->
                                for (rotation in 0..5) {
                                    list.add(
                                        HardBotPossiblePlacements(
                                            tile = selectedTile,
                                            tilePlacement = tilePlace,
                                            rotation = rotation,
                                            wildlifeToken = selectedToken,
                                            usedNaturalToken = if (tokenChance == 1) 1; else 2,
                                            wildlifePlacement = animalPlace,
                                            wildLifeChance = tokenChance,
                                            customPair = Pair(i, selectedTokenIndex),
                                        )
                                    )
                                }
                            }
                            habitat.remove(tilePlace)
                        }
                    }
                }
            })
        }

        for (thread in threads) {
            thread.start()
        }

        for (thread in threads) {
            thread.join()
        }

        return list.toMutableList()
    }
}