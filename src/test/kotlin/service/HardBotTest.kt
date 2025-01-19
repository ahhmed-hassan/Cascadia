package service

import entity.PlayerType
import gui.Refreshables
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

/**
 * Test class for the HardBot
 */
class HardBotTest : Refreshables {
    private var gameIsActive = true

    /**
     * Tests the Hard Bot
     */
    @Test
    fun testHardBot() {
        assertDoesNotThrow {
            for (i in 1..2) {
                gameIsActive = true
                val rootService = RootService()
                rootService.addRefreshable(this)
                val players = listOf("A", "B", "C", "D")

                val playerMap = mutableMapOf<String, PlayerType>()

                for (j in 0..(i % 3 + 1)) {
                    playerMap[players[j]] = PlayerType.NORMAL
                }

                rootService.gameService.startNewGame(
                    playerNames = playerMap,
                    scoreRules = listOf(),
                    orderIsRandom = true,
                    isRandomRules = true
                )

                rootService.currentGame?.playerList?.forEach { player -> player.natureToken = 30 }

                (1..playerMap.size * 20).forEach { _ -> if (gameIsActive) rootService.hardBotService.takeTurn() }
            }
        }

        //Tests if there is an Exception when a non HardBot trys to use the takeTurn method
        assertThrows<AssertionError> {
            gameIsActive = true
            val rootService = RootService()
            rootService.addRefreshable(this)

            val players = mapOf("A" to PlayerType.NORMAL, "B" to PlayerType.LOCAL)

            rootService.gameService.startNewGame(
                playerNames = players,
                scoreRules = listOf(),
                orderIsRandom = true,
                isRandomRules = true
            )

            (1..2).forEach { _ -> if (gameIsActive) rootService.hardBotService.takeTurn() }
        }
    }

    override fun refreshAfterGameEnd(scores: Map<String, ScoringService.Companion.PlayerScore>) {
        gameIsActive = false
    }
}