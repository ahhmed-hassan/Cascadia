package service
import entity.*
import kotlin.test.*

/**
 *  Test class for the calculateHawkScore method in the class scoringService.
 */
class CalculateHawkScoreTest {

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.HAWK] for scoring rule A
     */
    @Test
    fun testScoreCalculationA() {

        // set up test object
        val testRootService = RootService()
        val testScoringService = ScoringService(testRootService)
        val testGameService = GameService(testRootService)

        testGameService.startNewGame(mapOf(Pair("testPlayer", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(false, false, false, false, false),
            orderIsRandom = false, isRandomRules = false)

        val testGame = testRootService.currentGame
        checkNotNull(testGame)
        val testPlayer = testGame.playerList[0]
        createHabitat(testPlayer)


        //tests the function when the hawk is surrounded by other hawks
        assertEquals(0 , testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is still 0 when it is surrounded by 5 other animals
        checkNotNull(testPlayer.habitat[Pair(-1, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 0, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 1, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 1,-1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 0,-1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(0 , testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is 2 when one hawk is not surrounded by any hawks
        checkNotNull(testPlayer.habitat[Pair( -1,0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(2 , testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is 5 when two hawks are not surrounded by any hawks
        checkNotNull(testPlayer.habitat[Pair( -2,0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -3,1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -4,1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -4,0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -3,-1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -2,-1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -1,-1)]).wildlifeToken = WildlifeToken(Animal.ELK)

        assertEquals(5 , testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is 2 when the other hawk is not surrounded by any other hawks
        checkNotNull(testPlayer.habitat[Pair( 0,0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(2 , testScoringService.calculateHawkScore(testPlayer))

        //tests if the score is 0 when there are no hawks on the habitat
        checkNotNull(testPlayer.habitat[Pair( -3,0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(0 , testScoringService.calculateHawkScore(testPlayer))

    }

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.HAWK] for scoring rule B
     */
    @Test
    fun testScoreCalculationB() {

        // set up test object
        val testRootService = RootService()
        val testScoringService = ScoringService(testRootService)
        val testGameService = GameService(testRootService)

        testGameService.startNewGame(mapOf(Pair("testPlayer1", PlayerType.LOCAL), Pair("testPlayer2", PlayerType.EASY)),
            scoreRules = listOf(true, true, true, true, true),
            orderIsRandom = false, isRandomRules = false, )

        val testGame = testRootService.currentGame
        checkNotNull(testGame)
        val testPlayer = testGame.playerList[0]
        createHabitat(testPlayer)


        //tests the function when the hawk is surrounded by other hawks
        assertEquals(0 , testScoringService.calculateHawkScore(testPlayer))

        //tests horizontal with two hawks
        checkNotNull(testPlayer.habitat[Pair( 0, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 0, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -1, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 0, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 1, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -1, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -2, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -2, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(5 , testScoringService.calculateHawkScore(testPlayer))

        //tests with three Hawks diagonal
        checkNotNull(testPlayer.habitat[Pair( 1, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -3, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 0, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -1, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( 1, -1)]).wildlifeToken = WildlifeToken(Animal.HAWK)
        checkNotNull(testPlayer.habitat[Pair( -1, 1)]).wildlifeToken = WildlifeToken(Animal.HAWK)
        checkNotNull(testPlayer.habitat[Pair( -1, -1)]).wildlifeToken = WildlifeToken(Animal.HAWK)
        assertEquals(9 , testScoringService.calculateHawkScore(testPlayer))

        //tests with four tiles
        checkNotNull(testPlayer.habitat[Pair( -3, -1)]).wildlifeToken = WildlifeToken(Animal.HAWK)
        checkNotNull(testPlayer.habitat[Pair( -4, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair( -3, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(12 , testScoringService.calculateHawkScore(testPlayer))

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
            testTiles.add(HabitatTile(i,
                false,
                0,
                listOf(Animal.HAWK, Animal.ELK),
                WildlifeToken(Animal.HAWK),
                mutableListOf(Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,
                    Terrain.WETLAND,)))
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

        player.habitat[Pair(-1, -1)] = testTiles[14]

    }
}