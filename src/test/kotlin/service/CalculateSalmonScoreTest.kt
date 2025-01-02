package service

import entity.*
import kotlin.test.*

/**
 *  Test class for method [ScoringService.calculateSalmonScore] in service
 */
class CalculateSalmonScoreTest {

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.SALMON] for scoring rule A
     */
    @Test
    fun testScoreCalculationA() {

        // set up test object
        val testScoringService = ScoringService(RootService())

        // create test player
        val testPlayer = createTestPlayer()

        // test run of one
        var changeTile = checkNotNull(testPlayer.habitat[Pair(0,0)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(2, testScoringService.calculateSalmonScore(testPlayer))

        // test run of two
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,-1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(5, testScoringService.calculateSalmonScore(testPlayer))

        // test run of three
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,0)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(8, testScoringService.calculateSalmonScore(testPlayer))

        // test insularity of triangular run of three
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        // adding a fourth salmon to a triangular run of three yields no points
        assertEquals(0, testScoringService.calculateSalmonScore(testPlayer))

        // test run of four
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,2)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,-1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(12, testScoringService.calculateSalmonScore(testPlayer))

        //test run of five
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,3)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(16, testScoringService.calculateSalmonScore(testPlayer))

        //test run of six
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,4)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(20, testScoringService.calculateSalmonScore(testPlayer))

        //test run of seven
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,5)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(25, testScoringService.calculateSalmonScore(testPlayer))

        //test run of more than seven
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,6)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(25, testScoringService.calculateSalmonScore(testPlayer))
    }


    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.BEAR] for scoring rule B
     */
    @Test
    fun testScoreCalculationB() {
        // set up test object
        val testScoringService = ScoringService(RootService())

        // create player
        val testPlayer = createTestPlayer()

        // test with no salmon run
        assertEquals(0, testScoringService.calculateSalmonScore(testPlayer))

        // test run of one
        var changeTile = checkNotNull(testPlayer.habitat[Pair(0,0)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(2, testScoringService.calculateSalmonScore(testPlayer))

        // test run of two
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,-1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(4, testScoringService.calculateSalmonScore(testPlayer))

        // test run of three
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,0)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(9, testScoringService.calculateSalmonScore(testPlayer))

        // test insularity of triangular run of three
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        // adding a fourth salmon to a triangular run of three yields no points
        assertEquals(0, testScoringService.calculateSalmonScore(testPlayer))

        // test run of four
        // change current run of three to not be a triangle
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,-1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.ELK)
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        // add fourth salmon
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,2)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(11, testScoringService.calculateSalmonScore(testPlayer))

        //test run of five
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,3)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(17, testScoringService.calculateSalmonScore(testPlayer))

        //test run of more than five
        changeTile = checkNotNull(testPlayer.habitat[Pair(1,4)])
        changeTile.wildlifeToken = WildlifeToken(Animal.SALMON)

        assertEquals(17, testScoringService.calculateSalmonScore(testPlayer))

    }

    /**
     *  Helper method to create the [Player] needed for score calculation.
     *  Initialize this player with a habitat designed for testing the salmon score calculation.
     *
     *  @return an instance of [Player] with a test habitat
     */
    private fun createTestPlayer() : Player {

        // create tiles
        val testTiles = mutableListOf<HabitatTile>()
        for (i in 1..9) {
            testTiles.add(HabitatTile(i,
                false,
                0,
                listOf(Animal.SALMON, Animal.ELK),
                WildlifeToken(Animal.ELK),
                mutableListOf(Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,)))
        }

        // create habitat
        val testHabitat = mutableMapOf<Pair<Int, Int>, HabitatTile>()
        testHabitat[Pair(0, 0)] = testTiles[0]
        testHabitat[Pair(1,-1)] = testTiles[1]
        testHabitat[Pair(1, 0)] = testTiles[2]
        testHabitat[Pair(1, 1)] = testTiles[3]
        testHabitat[Pair(1, 2)] = testTiles[4]
        testHabitat[Pair(1, 3)] = testTiles[5]
        testHabitat[Pair(1, 4)] = testTiles[6]
        testHabitat[Pair(1, 5)] = testTiles[7]
        testHabitat[Pair(1, 6)] = testTiles[8]

        return Player(name= "testPlayer",
            habitat= testHabitat,
            playerType= PlayerType.LOCAL)

    }

}
