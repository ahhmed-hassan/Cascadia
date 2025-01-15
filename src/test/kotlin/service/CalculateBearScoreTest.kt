package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *  test class for method [ScoringService.calculateBearScore] in service
 */
class CalculateBearScoreTest {

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.BEAR] for scoring rule A
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
        setUpHabitat(testPlayer)


        // test no exact pair of bears
        assertEquals(0, testScoringService.calculateBearScore(testPlayer.habitat))

        // test one pair of bears
        var separatorTile = checkNotNull(testPlayer.habitat[Pair(1, 1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(4, testScoringService.calculateBearScore(testPlayer.habitat))

        // test two pairs
        separatorTile = checkNotNull(testPlayer.habitat[Pair(0, 0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(11, testScoringService.calculateBearScore(testPlayer.habitat))

        // test three pairs
        separatorTile = checkNotNull(testPlayer.habitat[Pair(-1, 0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        separatorTile = checkNotNull(testPlayer.habitat[Pair(-1, 1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)


        assertEquals(19, testScoringService.calculateBearScore(testPlayer.habitat))

        // test four pairs
        separatorTile.wildlifeToken = WildlifeToken(Animal.BEAR)

        assertEquals(27, testScoringService.calculateBearScore(testPlayer.habitat))

    }


    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.BEAR] for scoring rule B
     */
    @Test
    fun testScoreCalculationB() {
        // set up test object
        val testRootService = RootService()
        val testScoringService = ScoringService(testRootService)
        val testGameService = GameService(testRootService)

        testGameService.startNewGame(
            mapOf(Pair("testPlayer", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(true, true, true, true, true),
            orderIsRandom = false, isRandomRules = false
        )

        val testGame = testRootService.currentGame
        checkNotNull(testGame)
        val testPlayer = testGame.playerList[0]
        setUpHabitat(testPlayer)


        // test no exact tripple of bears
        assertEquals(0, testScoringService.calculateBearScore(testPlayer.habitat))

        // test one tripple of bears
        var separatorTile = checkNotNull(testPlayer.habitat[Pair(1, 0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(10, testScoringService.calculateBearScore(testPlayer.habitat))

        // test two tripple of bears
        separatorTile = checkNotNull(testPlayer.habitat[Pair(0, 0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)
        separatorTile = checkNotNull(testPlayer.habitat[Pair(-1, -1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(20, testScoringService.calculateBearScore(testPlayer.habitat))

    }

    /**
     *  Helper method to change the habitat of the test for score calculation.
     *
     *  @param testPlayer is the player whose habitat shall be changed
     */
    private fun setUpHabitat(testPlayer: Player) {

        // create tiles
        val testTiles = mutableListOf<HabitatTile>()
        for (i in 1..11) {
            testTiles.add(
                HabitatTile(
                    i,
                    false,
                    0,
                    listOf(Animal.BEAR, Animal.ELK),
                    WildlifeToken(Animal.BEAR),
                    mutableListOf(
                        Terrain.WETLAND,
                        Terrain.WETLAND,
                        Terrain.WETLAND,
                        Terrain.WETLAND,
                        Terrain.WETLAND,
                        Terrain.WETLAND,
                    )
                )
            )
        }

        // set up custom habitat
        testPlayer.habitat.clear()
        testPlayer.habitat[Pair(0, 0)] = testTiles[0]
        testPlayer.habitat[Pair(1, -1)] = testTiles[1]
        testPlayer.habitat[Pair(1, 0)] = testTiles[2]
        testPlayer.habitat[Pair(1, 1)] = testTiles[3]
        testPlayer.habitat[Pair(1, 2)] = testTiles[4]
        testPlayer.habitat[Pair(1, 3)] = testTiles[5]
        testPlayer.habitat[Pair(-1, -1)] = testTiles[6]
        testPlayer.habitat[Pair(-1, -2)] = testTiles[7]
        testPlayer.habitat[Pair(-1, 0)] = testTiles[8]
        testPlayer.habitat[Pair(-1, 1)] = testTiles[9]
        testPlayer.habitat[Pair(-1, 2)] = testTiles[10]

    }


}