package service

import entity.*
import kotlin.test.*

/**
 * Test class for the [ScoringService] class to validate the functionality of the calculateBonusScores method.
 * This class includes tests for various scenarios such as two-player games, three or more player games,
 * and edge cases like null values or uninitialized games.
 */
class CalculateBonusScoresTest {
    private val rootService = RootService()
    private val scoringService = ScoringService(rootService)

    /**
     * Tests the calculateBonusScores method for a two-player scenario.
     * Ensures that the bonus scores are calculated correctly when there are only two players in the game.
     */
    @Test
    fun testBonusScores() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
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
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 1
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            )
        )

        println(playersLongestTerrain)

        val expectedBonusScores = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 0
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            )
        )

        println(expectedBonusScores)

        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        println(result)

        assertEquals(expectedBonusScores, result)
    }

    /**
     * Tests the calculateBonusScores method for a scenario with three or more players.
     * Ensures that the bonus scores are calculated correctly when there are more than two players in the game.
     */
    @Test
    fun testBonusScoresForThreeOrMorePlayers() {
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
                Terrain.FOREST to 3,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 1,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 1
            ), "Local_Player2" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            ), "Easy_Bot" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            )
        )
        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        assertEquals(expectedBonusScores, result)
    }

    /**
     * Tests the calculateBonusScores method when a players terrain map contains null values.
     */
    @Test
    fun testCalculateBonusScoresWithNullValue() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            )
        )

        assertFailsWith<IllegalStateException> {
            scoringService.calculateBonusScores(playersLongestTerrain)
        }
    }

    /**
     * Tests the calculateBonusScores method when the game is not started.
     */
    @Test
    fun testIfGameNotStarted() {
        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 1
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            )
        )
        assertFailsWith<IllegalStateException> {
            scoringService.calculateBonusScores(playersLongestTerrain)
        }
    }

    /**
     * Tests the calculateBonusScores method when the first player has longer terrains for all types.
     */
    @Test
    fun testFirstPlayerHasLongerTerrains() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 6,
                Terrain.MOUNTAIN to 4,
                Terrain.PRAIRIE to 3,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 1
            )
        )

        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        println(result)

        assertEquals(2, result["Local_Player1"]?.get(Terrain.FOREST))
        assertEquals(2, result["Local_Player1"]?.get(Terrain.MOUNTAIN))
        assertEquals(2, result["Local_Player1"]?.get(Terrain.PRAIRIE))
        assertEquals(2, result["Local_Player1"]?.get(Terrain.RIVER))
        assertEquals(2, result["Local_Player1"]?.get(Terrain.WETLAND))

        assertEquals(0, result["Local_Player2"]?.get(Terrain.FOREST))
        assertEquals(0, result["Local_Player2"]?.get(Terrain.MOUNTAIN))
        assertEquals(0, result["Local_Player2"]?.get(Terrain.PRAIRIE))
        assertEquals(0, result["Local_Player2"]?.get(Terrain.RIVER))
        assertEquals(0, result["Local_Player2"]?.get(Terrain.WETLAND))
    }

    /**
     * Tests the calculateBonusScores method when both players have equal terrains for all types.
     */
    @Test
    fun testEqualTerrains() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        val playersLongestTerrain = mapOf(
            "Local_Player1" to mapOf(
                Terrain.FOREST to 6,
                Terrain.MOUNTAIN to 4,
                Terrain.PRAIRIE to 3,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 6,
                Terrain.MOUNTAIN to 4,
                Terrain.PRAIRIE to 3,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            )
        )

        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        println(result)

        assertEquals(1, result["Local_Player1"]?.get(Terrain.FOREST))
        assertEquals(1, result["Local_Player1"]?.get(Terrain.MOUNTAIN))
        assertEquals(1, result["Local_Player1"]?.get(Terrain.PRAIRIE))
        assertEquals(1, result["Local_Player1"]?.get(Terrain.RIVER))
        assertEquals(1, result["Local_Player1"]?.get(Terrain.WETLAND))

        assertEquals(1, result["Local_Player2"]?.get(Terrain.FOREST))
        assertEquals(1, result["Local_Player2"]?.get(Terrain.MOUNTAIN))
        assertEquals(1, result["Local_Player2"]?.get(Terrain.PRAIRIE))
        assertEquals(1, result["Local_Player2"]?.get(Terrain.RIVER))
        assertEquals(1, result["Local_Player2"]?.get(Terrain.WETLAND))
    }
}