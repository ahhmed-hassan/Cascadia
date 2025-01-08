package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateFoxScoreTest {

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.FOX] for scoring rule A
     */
    @Test
    fun testScoreCalculationA() {

        // set up test object
        val testRootService = RootService()
        val testScoringService = ScoringService(testRootService)
        val testGameService = GameService(testRootService)

        testGameService.startNewGame(
            mapOf(Pair("testPlayer", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(false, false, false, false, false),
            orderIsRandom = false, isRandomRules = false
        )

        val testGame = testRootService.currentGame
        checkNotNull(testGame)
        val testPlayer = testGame.playerList[0]
        createHabitat(testPlayer)

        //tests the function when the fox is surrounded by other hawks
        assertEquals(0 , testScoringService.calculateFoxScore(testPlayer))


        checkNotNull(testPlayer.habitat[Pair(0, 0)]).wildlifeToken = WildlifeToken(Animal.FOX)
        assertEquals(1 , testScoringService.calculateFoxScore(testPlayer))

        //tests with one different neighbour
        checkNotNull(testPlayer.habitat[Pair(1, 0)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        assertEquals(2 , testScoringService.calculateFoxScore(testPlayer))

        //tests with two different neighbours
        checkNotNull(testPlayer.habitat[Pair(1, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(3 , testScoringService.calculateFoxScore(testPlayer))

        //tests with three different neighbours
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.SALMON)
        assertEquals(4 , testScoringService.calculateFoxScore(testPlayer))

        //tests with four different neighbours when one is a fox so it is a score of 5+3
        checkNotNull(testPlayer.habitat[Pair(-1, 0)]).wildlifeToken = WildlifeToken(Animal.FOX)
        assertEquals(8 , testScoringService.calculateFoxScore(testPlayer))

        //tests another fox on the map
        checkNotNull(testPlayer.habitat[Pair(-3, 0)]).wildlifeToken = WildlifeToken(Animal.FOX)
        assertEquals(9 , testScoringService.calculateFoxScore(testPlayer))

        //tests if the score gets added up when you add another salmon on to the new fox
        checkNotNull(testPlayer.habitat[Pair(-4, 0)]).wildlifeToken = WildlifeToken(Animal.SALMON)
        assertEquals(10 , testScoringService.calculateFoxScore(testPlayer))

        //checks if you add a bear between to foxes
        checkNotNull(testPlayer.habitat[Pair(-2, 0)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        assertEquals(12 , testScoringService.calculateFoxScore(testPlayer))
    }

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.FOX] for scoring rule B
     */
    @Test
    fun testScoreCalculationB() {

        // set up test object
        val testRootService = RootService()
        val testScoringService = ScoringService(testRootService)
        val testGameService = GameService(testRootService)

        testGameService.startNewGame(
            mapOf(Pair("testPlayer1", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(true, true, true, true, true),
            orderIsRandom = false, isRandomRules = false,
        )

        val testGame = testRootService.currentGame
        checkNotNull(testGame)
        val testPlayer = testGame.playerList[0]
        createHabitat(testPlayer)


        //tests the function when the hawk is surrounded by other hawks
        assertEquals(0, testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is 3 when the fox is surrounded by hawks
        checkNotNull(testPlayer.habitat[Pair(0, 0)]).wildlifeToken = WildlifeToken(Animal.FOX)
        assertEquals(3, testScoringService.calculateFoxScore(testPlayer))

        //tests with 2 pairs
        checkNotNull(testPlayer.habitat[Pair(1, -1)]).wildlifeToken = WildlifeToken(Animal.SALMON)
        checkNotNull(testPlayer.habitat[Pair(-1, 0)]).wildlifeToken = WildlifeToken(Animal.SALMON)
        assertEquals(5, testScoringService.calculateFoxScore(testPlayer))

        //tests with 3 pairs
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        checkNotNull(testPlayer.habitat[Pair(0, 1)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        assertEquals(7, testScoringService.calculateFoxScore(testPlayer))

        //tests with another fox
        checkNotNull(testPlayer.habitat[Pair(-3, 0)]).wildlifeToken = WildlifeToken(Animal.FOX)
        assertEquals(10, testScoringService.calculateFoxScore(testPlayer))

        checkNotNull(testPlayer.habitat[Pair(-3, 1)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        checkNotNull(testPlayer.habitat[Pair(-4, 0)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        assertEquals(12, testScoringService.calculateFoxScore(testPlayer))

    }
    /**
     *  the function creates the Habitat for tests
     *
     *  @param player is the player whose habitat shall be changed
     */
    private fun createHabitat(player: Player) {

        // create tiles
        val testTiles = mutableListOf<HabitatTile>()
        for (i in 1..15) {
            testTiles.add(
                HabitatTile(i,
                false,
                0,
                listOf(Animal.ELK, Animal.BEAR, Animal.HAWK, Animal.SALMON, Animal.FOX),
                WildlifeToken(Animal.HAWK),
                mutableListOf(
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,))
            )
        }

        // create habitat
        player.habitat[Pair( 0, 0)] = testTiles[0]
        player.habitat[Pair(-1, 1)] = testTiles[1]
        player.habitat[Pair( 0, 1)] = testTiles[2]
        player.habitat[Pair( 1, 0)] = testTiles[3]
        player.habitat[Pair( 1, -1)] = testTiles[4]
        player.habitat[Pair( 0, -1)] = testTiles[5]
        player.habitat[Pair(-1, 0)] = testTiles[6]

        player.habitat[Pair(-2, 0)] = testTiles[7]
        player.habitat[Pair(-3, 1)] = testTiles[8]
        player.habitat[Pair(-4, 1)] = testTiles[9]
        player.habitat[Pair(-4, 0)] = testTiles[10]
        player.habitat[Pair(-3, -1)] = testTiles[11]
        player.habitat[Pair(-2, -1)] = testTiles[12]
        player.habitat[Pair(-3, 0)] = testTiles[13]

    }
}