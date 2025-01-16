package service

import entity.*
import kotlin.test.*

class CalculateBonusForThreeOrMorePlayersTest {
    private val rootService = RootService()
    private val scoringService = ScoringService(rootService)

    @Test
    fun testBonusScoresForFourPlayers() {
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
            ),
            "Easy_Bot1" to mapOf(
                Terrain.FOREST to 4,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 5,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ),
            "Easy_Bot2" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 3,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 1
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
            ),
            "Easy_Bot1" to mapOf(
                Terrain.FOREST to 0,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 2,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 2
            ),
            "Easy_Bot2" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 0
            )
        )

        println(expectedBonusScores)

        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        println(result)

        assertEquals(expectedBonusScores, result)
    }

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
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 5,
                Terrain.MOUNTAIN to 5,
                Terrain.PRAIRIE to 4,
                Terrain.RIVER to 3,
                Terrain.WETLAND to 4
            ),
            "Easy_Bot" to mapOf(
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
                Terrain.MOUNTAIN to 2,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ),
            "Local_Player2" to mapOf(
                Terrain.FOREST to 2,
                Terrain.MOUNTAIN to 2,
                Terrain.PRAIRIE to 0,
                Terrain.RIVER to 2,
                Terrain.WETLAND to 2
            ),
            "Easy_Bot" to mapOf(
                Terrain.FOREST to 1,
                Terrain.MOUNTAIN to 1,
                Terrain.PRAIRIE to 3,
                Terrain.RIVER to 1,
                Terrain.WETLAND to 1
            )
        )

        println(expectedBonusScores)

        val result = scoringService.calculateBonusScores(playersLongestTerrain)
        println(result)

        assertEquals(expectedBonusScores, result)
    }
}