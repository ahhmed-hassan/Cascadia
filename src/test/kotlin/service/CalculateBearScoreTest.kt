package service

import entity.*
import java.io.File
import java.util.*
import kotlin.test.*

/**
 *  test class for method [ScoringService.calculateBearScore] in service
 */
class CalculateBearScoreTest {

    @Test
    fun testArgument() {

        // set up test object
        val testScoringService = ScoringService(RootService())

        // create test value

    }

    @Test
    fun testScoreCalculationA() {

        // set up test object
        val testScoringService = ScoringService(RootService())

        // create test values
        // create tiles
        val testTiles = mutableListOf<HabitatTile>()
        for (i in 1..6) {
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
        // Designate one tile as seperator between bear groups
        testTiles[3].wildlifeToken = WildlifeToken(Animal.ELK)

        // create habitat
        val testHabitat = mutableMapOf<Pair<Int, Int>, HabitatTile>()
        testHabitat[Pair(0,0)]  = testTiles[0]
        testHabitat[Pair(1,-1)] = testTiles[1]
        testHabitat[Pair(1,0)]  = testTiles[2]
        testHabitat[Pair(0,1)]  = testTiles[3]
        testHabitat[Pair(0,2)]  = testTiles[4]
        testHabitat[Pair(1,2)]  = testTiles[5]

        // create player
        val testPlayer = Player(name= "testPlayer",
                                habitat= testHabitat,
                                playerType= PlayerType.LOCAL)



    }

}