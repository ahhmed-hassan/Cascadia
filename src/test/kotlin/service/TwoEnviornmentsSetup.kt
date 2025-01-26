package service

import entity.*
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TwoEnviornmentsSetup {
    companion object {
        private fun dummyHabitat(animal: Animal) =
            HabitatTile(
                id = Random.nextInt(),
                isKeystoneTile = Random.nextBoolean(),
                rotationOffset = Random.nextInt(),
                wildlifeSymbols = listOf(Animal.values().random()),
                wildlifeToken = WildlifeToken(animal),
                terrains = List(6) { Terrain.values().random() }.toMutableList()
            )

        private fun nullHabitat() = HabitatTile(
            id = Random.nextInt(),
            isKeystoneTile = Random.nextBoolean(),
            rotationOffset = Random.nextInt(),
            wildlifeSymbols = listOf(Animal.values().random()),
            wildlifeToken = null,
            terrains = List(6) { Terrain.values().random() }.toMutableList()
        )

        fun group2Habitat(): Map<Pair<Int, Int>, HabitatTile> {
            val res = mapOf(
                //first Row
                (-2 to 0) to dummyHabitat(Animal.FOX),
                (-2 to 1) to nullHabitat(),
                //SecondRow
                (-1 to -1) to dummyHabitat(Animal.BEAR),
                (-1 to 0) to dummyHabitat(Animal.BEAR),
                (-1 to 1) to dummyHabitat(Animal.HAWK),
                (-1 to 2) to dummyHabitat(Animal.FOX),
                // Third
                (0 to -4) to dummyHabitat(Animal.FOX),
                (0 to -3) to dummyHabitat(Animal.HAWK),
                (0 to -2) to dummyHabitat(Animal.ELK),
                (0 to -1) to dummyHabitat(Animal.SALMON),
                (0 to 0) to dummyHabitat(Animal.HAWK),
                (0 to 1) to dummyHabitat(Animal.BEAR),
                (0 to 2) to nullHabitat(),
                //Fourth
                (1 to -3) to nullHabitat(),
                (1 to -2) to dummyHabitat(Animal.BEAR),
                (1 to -1) to dummyHabitat(Animal.HAWK),
                (1 to 0) to dummyHabitat(Animal.SALMON),
                (1 to 1) to dummyHabitat(Animal.HAWK),
                (1 to 2) to nullHabitat(),
                //Fifth
                (2 to -1) to dummyHabitat(Animal.BEAR),
                (2 to 0) to nullHabitat(),
                (2 to 1) to nullHabitat()
            )
            return res
        }

        fun leoHabitat(): Map<Pair<Int, Int>, HabitatTile> {
            val res = mapOf(
                //First row
                (-5 to 2) to dummyHabitat(Animal.SALMON),
                //second
                (-4 to 1) to dummyHabitat(Animal.SALMON),
                (-4 to 2) to dummyHabitat(Animal.FOX),
                //third
                (-3 to 0) to dummyHabitat(Animal.SALMON),
                (-3 to 1) to dummyHabitat(Animal.HAWK),
                //Fourth
                (-2 to -1) to dummyHabitat(Animal.SALMON),
                (-2 to 0) to nullHabitat(),
                (-2 to 1) to dummyHabitat(Animal.FOX),
                (-2 to 2) to dummyHabitat(Animal.ELK),
                //Fifth
                (-1 to -1) to dummyHabitat(Animal.SALMON),
                (-1 to 0) to dummyHabitat(Animal.HAWK),
                (-1 to 1) to dummyHabitat(Animal.ELK),
                (-1 to 0) to dummyHabitat(Animal.BEAR),
                //Sixth
                (0 to -2) to dummyHabitat(Animal.SALMON),
                (0 to -1) to nullHabitat(),
                (0 to 0) to dummyHabitat(Animal.ELK),
                (0 to 1) to dummyHabitat(Animal.ELK),
                (0 to 2) to dummyHabitat(Animal.BEAR),
                //Seventh
                (1 to -1) to dummyHabitat(Animal.ELK),
                (1 to 0) to dummyHabitat(Animal.HAWK),
                (1 to 1) to dummyHabitat(Animal.FOX),
                (1 to 2) to nullHabitat(),
                //Eighth
                (2 to 0) to nullHabitat()


            )
            return res
        }


    }

    val rootService = RootService()

    @BeforeTest
    fun setup() {
        rootService.currentGame = null
        rootService.gameService.startNewGame(
            mapOf(Pair("testPlayer", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(true, false, true, false, true),
            orderIsRandom = false, isRandomRules = false
        )
        val game = checkNotNull(rootService.currentGame)

        game.playerList.forEach { it.habitat.clear() }
    }

    @Test
    fun testSalmonForGroup2() {
        val game = checkNotNull(rootService.currentGame)
        assertTrue(checkNotNull(game.ruleSet[Animal.SALMON.ordinal]))
        game.currentPlayer.habitat.putAll(group2Habitat())
        val salmonScore = rootService.scoringService.calculateSalmonScore(game.currentPlayer.habitat)
        assertEquals(4, salmonScore)
    }

    @Test
    fun testSalmonForLeo() {
        val game = checkNotNull(rootService.currentGame)
        assertTrue(checkNotNull(game.ruleSet[Animal.SALMON.ordinal]))
        game.currentPlayer.habitat.putAll(leoHabitat())
        val salmonScore = rootService.scoringService.calculateSalmonScore(game.currentPlayer.habitat)
        assertEquals(17, salmonScore)
    }
}