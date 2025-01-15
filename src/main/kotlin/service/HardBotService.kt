package service

import entity.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class HardBotService(private val rootService: RootService) {
    val gameService = GameService(rootService)

    val animalPlacingChance = 80

    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        require(game.currentPlayer.playerType == PlayerType.NORMAL)

        val thread = Thread {
            takeAsyncTurn(game)
        }
        thread.start()
        thread.join()
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun takeAsyncTurn(game: CascadiaGame) = runBlocking {

        val tiles = game.habitatTileList.shuffled()
        val animalTokens = game.wildlifeTokenList.shuffled().toMutableList()
        val numberOfPlayers = game.playerList.size
        val currentRound =
            if (tiles.size % numberOfPlayers == 0) tiles.size / numberOfPlayers else tiles.size / numberOfPlayers + 1

        val queue = ConcurrentLinkedQueue<HardBotJob>()
        val timeIsUp = AtomicBoolean(false)
        val threads = Collections.synchronizedList(mutableListOf<Thread>())

        val sceduler = launch {
            println("Launched")
            delay(8000)
            //Todo: start first calcualtion
            delay(1000)
            timeIsUp.set(false)
            synchronized(threads) {
                threads.forEach { it.interrupt() }
            }
            delay(50)
            //Todo: start second calcualtion
            delay(500)
            //TODO: TakeRealTurn()
            println("It Worked!")
        }
        println(Thread.currentThread().name)
        println("test")

        val possibilities = calculateAllPossibilities(game, animalTokens)

        launch {
            if (currentRound == 1) {
                possibilities.forEach { placement ->
                    createJob(
                        employer = placement,
                        game = game,
                        queue = queue,
                        round = currentRound
                    )
                }
            } else {
                while (!timeIsUp.get()) {
                    possibilities.forEach { possibility ->
                        createJob(
                            employer = possibility,
                            game = game,
                            queue = queue,
                            round = currentRound
                        )
                    }
                }
            }
        }

        var maxNumberOfThreads = Runtime.getRuntime().availableProcessors()
        for (i in 1..maxNumberOfThreads) {
            threads.add(
                Thread {
                    val rootService = RootService()
                    rootService.gameService.startNewGame(
                        playerNames = mapOf("A" to PlayerType.NORMAL, "B" to PlayerType.NORMAL),
                        scoreRules = listOf(),
                        orderIsRandom = true,
                        isRandomRules = true,
                    )
                    simulate(queue, rootService)
                }
            )
        }
        threads.forEach { it.start() }
    }

    private fun simulate(queue: ConcurrentLinkedQueue<HardBotJob>, rootService: RootService) {
        val gameService = rootService.gameService
        while (!Thread.interrupted()) {
            val job = queue.poll()
            if (job == null) {
                Thread.onSpinWait()
                continue
            }
            (1 until job.round).forEach { _ ->
                val tilePositions = gameService.getAllPossibleCoordinatesForTilePlacing(job.habitat)
                val tile = job.shop[0].first
                val animal = job.shop[0].second
                checkNotNull(tile)
                checkNotNull(animal)
                for (i in 1..(0..5).random()) {
                    rotateTile(tile)
                }
                job.habitat[tilePositions.random()] = tile

                if (animalPlacingChance >= (0..100).random()) {
                    val animalPositions = gameService.getAllPossibleTilesForWildlife(animal.animal, job.habitat)
                    if (animalPositions.isNotEmpty()) {
                        animalPositions.random().wildlifeToken = animal
                    }
                }

                job.shop[0] = Pair(job.habitatTiles.removeFirstOrNull(), job.animals.removeFirstOrNull())
            }
            val points = scoringService(job.habitat, rootService)
            job.employer.score += points
            job.employer.numberOfScores += 1
        }
    }

    private fun scoringService(habitat: MutableMap<Pair<Int, Int>, HabitatTile>, rootService: RootService): Int {
        val service = rootService.scoringService
        var points = 0
        points += service.calculateBearScore(habitat)
        points += service.calculateSalmonScore(habitat)
        points += service.calculateElkScore(habitat)
        points += service.calculateFoxScore(habitat)
        points += service.calculateHawkScore(habitat)

//        points += service.calculateLongestTerrain(Terrain.WETLAND, habitat)
//        points += service.calculateLongestTerrain(Terrain.RIVER, habitat)
//        points += service.calculateLongestTerrain(Terrain.FOREST, habitat)
//        points += service.calculateLongestTerrain(Terrain.PRAIRIE, habitat)
//        points += service.calculateLongestTerrain(Terrain.MOUNTAIN, habitat)

        return points
    }

    private fun createJob(
        employer: HardBotPossiblePlacements,
        game: CascadiaGame,
        queue: ConcurrentLinkedQueue<HardBotJob>,
        round: Int
    ) {
        val currentPlayer = game.currentPlayer
        val habitat = deepCopyHabitat(currentPlayer.habitat)
        val habitatTiles = mutableListOf<HabitatTile>()
        game.habitatTileList.forEach { habitatTiles.add(deepCopyHabitatTile(it)) }
        habitatTiles.shuffle()
        val animals = game.wildlifeTokenList.shuffled().toMutableList()
        val shop = deepCopyShop(game.shop)

        val tile = shop.find { it.first?.id == employer.tileId }?.first
        checkNotNull(tile)

        if (employer.replacedWildlife) {
            val customPair = checkNotNull(employer.customPair)
            val wildlifeToken = checkNotNull(employer.wildlifeToken)
            animals.add(checkNotNull(shop[customPair.second].second))
            shop[customPair.second] =
                Pair(shop[customPair.second].first, animals.find { it.animal == wildlifeToken.animal })
            animals.remove(shop[customPair.second].second)
            for (i in 0..3) {
                if (i == customPair.second) continue
                shop[i] = Pair(shop[i].first, animals.removeFirst())
            }
        }

        val customPair = employer.customPair
        if (customPair != null) {
            habitat[employer.tilePlacement] = checkNotNull(shop[customPair.first].first)
            habitat.forEach { habitatTile ->
                if (habitatTile.value.id == employer.wildlifePlacementId) {
                    habitatTile.value.wildlifeToken = shop[customPair.second].second
                }
            }
            shop[customPair.first] = Pair(habitatTiles.removeFirst(), shop[customPair.first].second)
            shop[customPair.second] = Pair(shop[customPair.second].first, animals.removeFirst())
        } else {
            val index = shop.indexOfFirst { it.first == tile }
            habitat[employer.tilePlacement] = checkNotNull(shop[index].first)
            habitat.forEach { habitatTile ->
                if (habitatTile.value.id == employer.wildlifePlacementId) {
                    habitatTile.value.wildlifeToken = shop[index].second
                }
            }
            shop[index] = Pair(habitatTiles.removeFirst(), animals.removeFirst())
        }
        val job = HardBotJob(
            naturalTokens = currentPlayer.natureToken - employer.usedNaturalToken,
            habitat = habitat,
            round = round,
            habitatTiles = habitatTiles,
            animals = animals,
            employer = employer,
            shop = shop
        )

        queue.add(job)
    }

    private fun calculateAllPossibilities(
        game: CascadiaGame,
        tokenList: MutableList<WildlifeToken>
    ): MutableList<HardBotPossiblePlacements> {
        val player = game.currentPlayer
        val possibleTilePlaces = gameService.getAllPossibleCoordinatesForTilePlacing(player.habitat)

        player.natureToken = 10

        val list = ConcurrentLinkedQueue<HardBotPossiblePlacements>()
        val threads = mutableListOf<Thread>()

        threads.add(Thread {
            val habitat = deepCopyHabitat(player.habitat)
            val shop = deepCopyShop(game.shop)
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
                                    tileId = first.id,
                                    tilePlacement = tilePlace,
                                    rotation = rotation,
                                    wildlifeToken = second,
                                    usedNaturalToken = 0,
                                    wildlifePlacementId = animalPlace.id,
                                    replacedWildlife = false
                                )
                            )
                        }
                    }
                    for (rotation in 0..5) {
                        list.add(
                            HardBotPossiblePlacements(
                                tileId = first.id,
                                tilePlacement = tilePlace,
                                rotation = rotation,
                                usedNaturalToken = 0,
                                replacedWildlife = false
                            )
                        )
                    }
                    habitat.remove(tilePlace)
                }
            }
        })

        if (player.natureToken > 0) {
            threads.add(Thread {
                val habitat = deepCopyHabitat(player.habitat)
                val shop = deepCopyShop(game.shop)
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

                        selectedToken = tokenList.first { it.animal == animal }
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
                                            tileId = selectedTile.id,
                                            tilePlacement = tilePlace,
                                            rotation = rotation,
                                            wildlifeToken = selectedToken,
                                            usedNaturalToken = if (tokenChance == 1) 1; else 2,
                                            wildlifePlacementId = animalPlace.id,
                                            wildLifeChance = tokenChance,
                                            customPair = Pair(i, selectedTokenIndex),
                                            replacedWildlife = tokenChance != 1
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

    private fun rotateTile(tile: HabitatTile) {
        tile.rotationOffset = (tile.rotationOffset - 1) % 6
        val first = tile.terrains.removeFirst()
        tile.terrains.add(
            tile.terrains.lastIndex,
            first
        )
    }

    private fun deepCopyHabitatTile(habitatTile: HabitatTile): HabitatTile {
        return HabitatTile(
            id = habitatTile.id,
            isKeystoneTile = habitatTile.isKeystoneTile,
            rotationOffset = habitatTile.rotationOffset,
            wildlifeSymbols = habitatTile.wildlifeSymbols.toList(),
            wildlifeToken = habitatTile.wildlifeToken?.copy(),
            terrains = habitatTile.terrains.toMutableList()
        )
    }

    private fun deepCopyHabitat(habitat: MutableMap<Pair<Int, Int>, HabitatTile>): MutableMap<Pair<Int, Int>, HabitatTile> {
        val newHabitat = mutableMapOf<Pair<Int, Int>, HabitatTile>()
        habitat.forEach { tile -> newHabitat[tile.key] = deepCopyHabitatTile(tile.value) }
        return newHabitat
    }

    private fun deepCopyShop(shop: MutableList<Pair<HabitatTile?, WildlifeToken?>>): MutableList<Pair<HabitatTile?, WildlifeToken?>> {
        val newShop = mutableListOf<Pair<HabitatTile?, WildlifeToken?>>()
        shop.forEach {
            it.second?.let { it1 -> Pair(it.first?.let { it2 -> deepCopyHabitatTile(it2) }, it1.copy()) }
                ?.let { it2 -> newShop.add(it2) }
        }
        return newShop
    }

}