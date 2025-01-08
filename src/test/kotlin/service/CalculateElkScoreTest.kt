package service
import entity.*
import kotlin.test.*

class CalculateElkScoreTest {

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.ELK] for scoring rule A
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

        // test no exact pair of bears
        assertEquals(0 , testScoringService.calculateElkScore(testPlayer))

        //check four tiles in a horizontal line
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(-1, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(-2, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(-3, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(13 , testScoringService.calculateElkScore(testPlayer))

        //checks if the tiles of the horizontal line gets removed first
        checkNotNull(testPlayer.habitat[Pair(-2, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(-3, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(18 , testScoringService.calculateElkScore(testPlayer))

        //checks if also the next line gets removed
        checkNotNull(testPlayer.habitat[Pair(-3, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(20 , testScoringService.calculateElkScore(testPlayer))
    }

    /**
     *  Test correct scoring for [HabitatTile]s with [Animal.ELK] for scoring rule B
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

        // test no exact pair of bears
        assertEquals(0 , testScoringService.calculateElkScore(testPlayer))

        //checks pattern with one tile
        checkNotNull(testPlayer.habitat[Pair(0, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(2 , testScoringService.calculateElkScore(testPlayer))

        //checks pattern with two tiles
        checkNotNull(testPlayer.habitat[Pair(-1, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(5 , testScoringService.calculateElkScore(testPlayer))

        //checks pattern with three tiles
        checkNotNull(testPlayer.habitat[Pair(-1, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(9 , testScoringService.calculateElkScore(testPlayer))

        //checks pattern with four tiles
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(13 , testScoringService.calculateElkScore(testPlayer))

        //checks the pattern with four titles with a rotation
        checkNotNull(testPlayer.habitat[Pair(0, 1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        assertEquals(13 , testScoringService.calculateElkScore(testPlayer))

        //checks if the tiles get removed
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(15 , testScoringService.calculateElkScore(testPlayer))

        //checks the score with 6 tiles and a pattern of 4 and 2
        checkNotNull(testPlayer.habitat[Pair(0, -1)]).wildlifeToken = WildlifeToken(Animal.BEAR)
        checkNotNull(testPlayer.habitat[Pair(-1, -1)]).wildlifeToken = WildlifeToken(Animal.ELK)
        checkNotNull(testPlayer.habitat[Pair(-2, 0)]).wildlifeToken = WildlifeToken(Animal.ELK)
        assertEquals(18 , testScoringService.calculateElkScore(testPlayer))

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
                listOf(Animal.ELK, Animal.BEAR),
                WildlifeToken(Animal.BEAR),
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
