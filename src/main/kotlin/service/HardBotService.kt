package service

import entity.*
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A class to manage the hard bot
 */
class HardBotService(private val rootService: RootService) {

    val gameService = rootService.gameService
    private val animalPlacingChance = 80

    /**
     * Simulating one turn of the bot
     */
    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        require(game.currentPlayer.playerType == PlayerType.NORMAL)

        val queue = ConcurrentLinkedQueue<HardBotJob>()
        val timeIsUp = AtomicBoolean(false)
        val threads = Collections.synchronizedList(mutableListOf<Thread>())
        val possibilities = mutableListOf<HardBotPossiblePlacements>()

        val thread = Thread {
            takeAsyncTurn(game, queue, timeIsUp, threads, possibilities)
        }
        thread.start()
        Thread.sleep(9000)
        timeIsUp.set(true)
        threads.forEach { it.interrupt() }
        thread.join()
        println("There are " + queue.size + " Jobs left")
        println("All stopped")

        val bestCertain =
            possibilities.filter { it.wildLifeChance == null || it.wildLifeChance == 1.0 }.maxByOrNull { it.getScore() }
        val bestUncertain =
            possibilities.filter { it.wildLifeChance != null && it.wildLifeChance != 1.0 }.maxByOrNull { it.getScore() }
        checkNotNull(bestCertain)

        bestCertain.score = 0 //TODO: Entfernen!!!

        var useCertain = true
        if (bestUncertain != null) {
            val bestUncertainWildlifeChance = bestUncertain.wildLifeChance
            useCertain = bestCertain.getScore() > bestUncertain.getScore() ||
                    bestUncertainWildlifeChance == null || bestUncertainWildlifeChance < 0.25 //TODO: auf 0.75 zuück setzen

            if (!useCertain) {
                val alternatives =
                    possibilities.filter {
                        it.tileId == bestUncertain.tileId
                                && it.tilePlacement == bestUncertain.tilePlacement
                                && it.rotation == bestUncertain.rotation
                                && it.wildlifeToken != bestUncertain.wildlifeToken
                    }
                alternatives.forEach {
                    val chance = it.wildLifeChance
                    if (chance != null && chance < 0.75) {
                        useCertain = true
                    }
                }
                if (alternatives.isEmpty()) useCertain = true
            }
        }

        if (useCertain) playCertain(game, bestCertain)
        else bestUncertain?.let { playUncertain(game, it, possibilities) }

    }

    private fun playUncertain(
        game: CascadiaGame,
        bestUncertain: HardBotPossiblePlacements,
        possibilities: MutableList<HardBotPossiblePlacements>
    ) {
        println("uncertain")
        rootService.playerActionService.replaceWildlifeTokens(listOf(0, 1, 2, 3))
        var animal = checkNotNull(bestUncertain.wildlifeToken)

        var shopWithAnimal = game.shop.filter { it.second?.animal == animal.animal }

        var actualPlacement = bestUncertain
        if (shopWithAnimal.isNotEmpty()) {
            chooseUncertainAnimalPair(bestUncertain, shopWithAnimal, game)
        } else {
            val animals = mutableListOf<WildlifeToken>()
            game.shop.forEach { if (!animals.contains(it.second)) it.second?.let { it1 -> animals.add(it1) } }
            val newPossiblePlacements = possibilities.filter {
                it.tileId == bestUncertain.tileId
                        && it.tilePlacement == bestUncertain.tilePlacement
                        && it.rotation == bestUncertain.rotation
                        && animals.contains(it.wildlifeToken)
            }.maxByOrNull { it.getScore() }
            if (newPossiblePlacements != null) {
                animal = checkNotNull(newPossiblePlacements.wildlifeToken)
                shopWithAnimal = game.shop.filter { it.second?.animal == animal.animal }

                chooseUncertainAnimalPair(newPossiblePlacements, shopWithAnimal, game)
                actualPlacement = newPossiblePlacements
            }
        }

        for (i in 0..actualPlacement.rotation) {
            rootService.playerActionService.rotateTile()
        }

        rootService.playerActionService.addTileToHabitat(actualPlacement.tilePlacement)

        if (actualPlacement.wildlifePlacementId != null) {
            game.currentPlayer.habitat.forEach { habitatAnimalTile ->
                if (habitatAnimalTile.value.id == actualPlacement.wildlifePlacementId) {
                    rootService.playerActionService.addToken(habitatAnimalTile.value)
                }
            }
        } else {
            rootService.playerActionService.discardToken()
        }
    }

    private fun chooseUncertainAnimalPair(
        placement: HardBotPossiblePlacements,
        shopWithAnimal: List<Pair<HabitatTile?,
                WildlifeToken?>>, game: CascadiaGame
    ) {
        val nicePair = shopWithAnimal.find { it.first?.id == placement.tileId }
        if (nicePair != null) {
            rootService.playerActionService.chooseTokenTilePair(game.shop.indexOf(nicePair))
        } else {
            val tileToPlace = game.shop.find { it.first?.id == placement.tileId }
            rootService.playerActionService.chooseCustomPair(
                game.shop.indexOf(tileToPlace),
                game.shop.indexOf(shopWithAnimal.first())
            )
        }
    }

    private fun playCertain(game: CascadiaGame, bestCertain: HardBotPossiblePlacements) {
        if (bestCertain.customPair != null) {
            rootService.playerActionService.chooseCustomPair(
                checkNotNull(bestCertain.customPair).first,
                checkNotNull(bestCertain.customPair).second
            )
        } else {
            val index = game.shop.indexOfFirst { it.first?.id == bestCertain.tileId }
            rootService.playerActionService.chooseTokenTilePair(index)
        }

        for (i in 0..bestCertain.rotation) {
            rootService.playerActionService.rotateTile()
        }

        rootService.playerActionService.addTileToHabitat(bestCertain.tilePlacement)

        if (bestCertain.wildlifePlacementId != null) {
            game.currentPlayer.habitat.forEach { habitatTile ->
                if (habitatTile.value.id == bestCertain.wildlifePlacementId) {
                    rootService.playerActionService.addToken(habitatTile.value)
                }
            }
        } else {
            rootService.playerActionService.discardToken()
        }
    }

    private fun takeAsyncTurn(
        game: CascadiaGame,
        queue: ConcurrentLinkedQueue<HardBotJob>,
        timeIsUp: AtomicBoolean,
        threads: MutableList<Thread>,
        possibilities: MutableList<HardBotPossiblePlacements>
    ) = runBlocking {

        val tiles = game.habitatTileList.shuffled()
        val animalTokens = game.wildlifeTokenList.shuffled().toMutableList()
        val numberOfPlayers = game.playerList.size
        val currentRound =
            if (tiles.size % numberOfPlayers == 0) tiles.size / numberOfPlayers else tiles.size / numberOfPlayers + 1

        possibilities.addAll(calculateAllPossibilities(game, animalTokens))
        println("There are " + possibilities.size + " possible Placements!")


        if (currentRound == 1) {
            Thread {
                possibilities.forEach { placement ->
                    createJob(
                        employer = placement,
                        game = game,
                        queue = queue,
                        round = currentRound
                    )
                }
            }.start()
        } else {
            Thread {
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
            }.start()
        }


        val maxNumberOfThreads = (Runtime.getRuntime().availableProcessors() - 2).coerceAtLeast(1)
        println("Available Threads: $maxNumberOfThreads")
        for (i in 1..maxNumberOfThreads) {
            threads.add(
                Thread {
                    simulate(queue, rootService)
                }
            )
        }
        threads.forEach { it.start() }
    }

    private fun simulate(queue: ConcurrentLinkedQueue<HardBotJob>, rootService: RootService) {
        val gameService = rootService.gameService
        var count = 0
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
                    animalPositions.takeIf { it.isNotEmpty() }?.random()?.wildlifeToken = animal
                }

                job.shop[0] = Pair(job.habitatTiles.removeFirstOrNull(), job.animals.removeFirstOrNull())
            }
            val points = scoringService(job.habitat, rootService)
            job.employer.score += points
            job.employer.numberOfScores += 1
            count++
            if (count % 1000 == 0) {
                println(
                    Thread.currentThread().name + " is in round $count of simulating!"
                )
            }
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

        points += service.calculateLongestTerrain(Terrain.WETLAND, habitat)
        points += service.calculateLongestTerrain(Terrain.RIVER, habitat)
        points += service.calculateLongestTerrain(Terrain.FOREST, habitat)
        points += service.calculateLongestTerrain(Terrain.PRAIRIE, habitat)
        points += service.calculateLongestTerrain(Terrain.MOUNTAIN, habitat)

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
            val curTile = checkNotNull(shop[customPair.first].first)
            for (i in 0..employer.rotation) rotateTile(curTile)
            habitat[employer.tilePlacement] = curTile
            habitat.forEach { habitatTile ->
                if (habitatTile.value.id == employer.wildlifePlacementId) {
                    habitatTile.value.wildlifeToken = shop[customPair.second].second
                }
            }
            shop[customPair.first] = Pair(habitatTiles.removeFirst(), shop[customPair.first].second)
            shop[customPair.second] = Pair(shop[customPair.second].first, animals.removeFirst())
        } else {
            val index = shop.indexOfFirst { it.first == tile }
            val curTile = checkNotNull(shop[index].first)
            for (i in 0..employer.rotation) rotateTile(curTile)
            habitat[employer.tilePlacement] = curTile
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
            round = round - 1,
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
                var selectedToken: WildlifeToken?
                var selectedTokenIndex = 0
                for (animal in Animal.values()) {
                    var tokenChance: Double?
                    if (animal in shop.map {
                            val token = checkNotNull(it.second)
                            token.animal
                        }) {
                        tokenChance = 1.0
                        val firstPair = shop.find { checkNotNull(it.second).animal == animal }
                        selectedToken = checkNotNull(firstPair).second
                        selectedTokenIndex = shop.indexOf(firstPair)
                    } else if (player.natureToken > 1) {
                        val k: Double = tokenList.filter {
                            it.animal == animal
                        }.size.toDouble()
                        if (k == 0.0) continue
                        val n = tokenList.size.toDouble()
                        val tokenChanceComplement: Double =
                            ((n - k) * (n - k - 1.0) * (n - k - 2.0) * (n - k - 3.0)) /
                                    (n * (n - 1.0) * (n - 2.0) * (n - 3.0))
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
                                            usedNaturalToken = if (tokenChance == 1.0) 1; else 2,
                                            wildlifePlacementId = animalPlace.id,
                                            wildLifeChance = tokenChance,
                                            customPair = Pair(i, selectedTokenIndex),
                                            replacedWildlife = tokenChance != 1.0
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

    private fun deepCopyShop(shop: MutableList<Pair<HabitatTile?, WildlifeToken?>>): MutableList<Pair<HabitatTile?,
            WildlifeToken?>> {
        val newShop = mutableListOf<Pair<HabitatTile?, WildlifeToken?>>()
        shop.forEach {
            it.second?.let { it1 -> Pair(it.first?.let { it2 -> deepCopyHabitatTile(it2) }, it1.copy()) }
                ?.let { it2 -> newShop.add(it2) }
        }
        return newShop
    }

}