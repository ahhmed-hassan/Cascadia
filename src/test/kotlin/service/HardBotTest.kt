package service

import entity.PlayerType
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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

        //Tests if there is an Exception when a non HardBot trys to use the takeTurn method
        assertThrows<IllegalArgumentException> {

            val rootService = RootService()

            val players = mapOf("A" to PlayerType.LOCAL, "B" to PlayerType.LOCAL)

            rootService.gameService.startNewGame(
                playerNames = players,
                scoreRules = listOf(),
                orderIsRandom = true,
                isRandomRules = true
            )

            (1..2).forEach { _ -> rootService.hardBotService.takeTurn() }
        }
    }
}