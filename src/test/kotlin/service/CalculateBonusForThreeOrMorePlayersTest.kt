package service

import entity.*
import kotlin.test.*

/**
 * Test class for the [ScoringService] to validate the bonus score calculation logic for different scenarios in a game.
 */
class CalculateBonusForThreeOrMorePlayersTest {
    private val rootService = RootService()
    private val scoringService = ScoringService(rootService)


    /**
     * Sets up the initial game state before test.
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL,
                "Easy_Bot1" to PlayerType.EASY,
                "Easy_Bot2" to PlayerType.EASY
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )
        val game = rootService.currentGame
        checkNotNull(game)
    }
    /**
     * Tests the bonus score calculation when there are four players in the game.
     * Each player's longest terrain lengths are predefined, and their bonus scores
     * are compared against the expected bonus scores.
     */
    @Test
    fun testBonusScoresForFourPlayers() {
        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 1
            ), "Local_Player2" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ), "Easy_Bot1" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ), "Easy_Bot2" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 1
            )
        )
        val expectedBonusScores = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 0
            ), "Local_Player2" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            ), "Easy_Bot1" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            ), "Easy_Bot2" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 0
            )
        )
        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        assertEquals(expectedBonusScores, result)
    }

    /**
     * Tests the bonus score calculation for a scenario where two players have equal
     * terrain lengths for all types of terrain, and a third player has different terrain lengths.
     * Validates the correct calculation of bonus scores in such cases.
     */
    @Test
    fun testEqualsScoresScenario() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL,
                "Easy_Bot" to PlayerType.EASY
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 5,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 3,
                Terrain.WETLAND to 4
            ), "Local_Player2" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 5,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 3,
                Terrain.WETLAND to 4
            ), "Easy_Bot" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            )
        )

        val expectedBonusScores = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 2,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ), "Local_Player2" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 2,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ), "Easy_Bot" to mapOf(
                Terrain.FOREST to 1,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 3,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 1
            )
        )
        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        assertEquals(expectedBonusScores, result)
    }
}