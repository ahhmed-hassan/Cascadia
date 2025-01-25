package service

import entity.PlayerType
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

/**
 * Test class for the HardBot
 */
class HardBotTest {

    /**
     * Tests the Hard Bot
     */
    @Test
    fun testHardBot() {
        assertDoesNotThrow {
            val rootService = RootService()
            val players = listOf("A", "B", "C", "D")

            val playerMap = mutableMapOf<String, PlayerType>()

            for (j in 0..3) {
                playerMap[players[j]] = PlayerType.NORMAL
            }

            rootService.gameService.startNewGame(
                playerNames = playerMap,
                scoreRules = listOf(),
                orderIsRandom = true,
                isRandomRules = true
            )
            rootService.currentGame?.playerList?.forEach { player -> player.natureToken = 20 }

            (1..2).forEach { _ -> rootService.hardBotService.takeTurn() }
        }
    }
}