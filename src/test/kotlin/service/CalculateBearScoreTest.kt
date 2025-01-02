package service

import entity.*
import kotlin.test.*

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
        val testScoringService = ScoringService(RootService())

        // create test player
        val testPlayer = createTestPlayer()

        // test no exact pair of bears
        assertEquals(0 , testScoringService.calculateBearScore(testPlayer))

        // test one pair of bears
        var separatorTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(4 , testScoringService.calculateBearScore(testPlayer))

        // test two pairs
        separatorTile = checkNotNull(testPlayer.habitat[Pair(0,0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(11 , testScoringService.calculateBearScore(testPlayer))

        // test three pairs
        separatorTile = checkNotNull(testPlayer.habitat[Pair(-1,0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        separatorTile = checkNotNull(testPlayer.habitat[Pair(-1,1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)


        assertEquals(19 , testScoringService.calculateBearScore(testPlayer))

        // test four pairs
        separatorTile.wildlifeToken = WildlifeToken(Animal.BEAR)

        assertEquals(27 , testScoringService.calculateBearScore(testPlayer))

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

        // test no exact tripple of bears
        assertEquals(0 , testScoringService.calculateBearScore(testPlayer))

        // test one tripple of bears
        var separatorTile = checkNotNull(testPlayer.habitat[Pair(1,0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(10 , testScoringService.calculateBearScore(testPlayer))

        // test two tripple of bears
        separatorTile = checkNotNull(testPlayer.habitat[Pair(1,0)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)
        separatorTile = checkNotNull(testPlayer.habitat[Pair(1,1)])
        separatorTile.wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(20 , testScoringService.calculateBearScore(testPlayer))

    }

    /**
     *  Helper method to create the [Player] needed for score calculation.
     *  Initialize this player with a habitat designed for testing the bear score calculation.
     *
     *  @return a instance of [Player] with a test habitat
     */
    private fun createTestPlayer() : Player {

        // create tiles
        val testTiles = mutableListOf<HabitatTile>()
        for (i in 1..11) {
            testTiles.add(HabitatTile(i,
                         false,
                          0,
                                      listOf(Animal.BEAR, Animal.ELK),
                                      WildlifeToken(Animal.BEAR),
                                      mutableListOf(Terrain.WETLAND,
                                                    Terrain.WETLAND,
                                                    Terrain.WETLAND,
                                                    Terrain.WETLAND,
                                                    Terrain.WETLAND,
                                                    Terrain.WETLAND,)))
        }

        // create habitat
        val testHabitat = mutableMapOf<Pair<Int, Int>, HabitatTile>()
        testHabitat[Pair( 0, 0)] = testTiles[0]
        testHabitat[Pair( 1,-1)] = testTiles[1]
        testHabitat[Pair( 1, 0)] = testTiles[2]
        testHabitat[Pair( 1, 1)] = testTiles[3]
        testHabitat[Pair( 1, 2)] = testTiles[4]
        testHabitat[Pair( 1, 3)] = testTiles[5]
        testHabitat[Pair(-1,-1)] = testTiles[6]
        testHabitat[Pair(-1,-2)] = testTiles[7]
        testHabitat[Pair(-1, 0)] = testTiles[8]
        testHabitat[Pair(-1, 1)] = testTiles[9]
        testHabitat[Pair(-1, 2)] = testTiles[10]


        return Player(name= "testPlayer",
                      habitat= testHabitat,
                      playerType= PlayerType.LOCAL)

    }

}