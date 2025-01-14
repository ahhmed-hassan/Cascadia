package service

import entity.PlayerType
import gui.Refreshables
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

/**
 * Class for testing the Easy Bot
 */
class EasyBotTest : Refreshables {

    private var gameIsActive = true

    /**
     * Tests the takeTurn Method of the easy bot by playing 1000 games.
     */
    @Test
    fun testEasyBot() {
        //Plays the game 1000 times
        assertDoesNotThrow {
            for (i in 1..1000) {
                gameIsActive = true
                val rootService = RootService()
                rootService.addRefreshable(this)
                val players = listOf("A", "B", "C", "D")

                val playerMap = mutableMapOf<String, PlayerType>()

                for (j in 0..(i % 3 + 1)) {
                    playerMap[players[j]] = PlayerType.EASY
                }

                rootService.gameService.startNewGame(
                    playerNames = playerMap,
                    scoreRules = listOf(),
                    orderIsRandom = true,
                    isRandomRules = true
                )

                (1..playerMap.size * 20).forEach { _ -> if (gameIsActive) rootService.easyBotService.takeTurn()}
            }
        }

        //Tests if there is an Exception when a non EasyBot trys to use the takeTurn method
        assertThrows<AssertionError> {
            gameIsActive = true
            val rootService = RootService()
            rootService.addRefreshable(this)

            val players = mapOf("A" to PlayerType.EASY, "B" to PlayerType.LOCAL)

            rootService.gameService.startNewGame(
                playerNames = players,
                scoreRules = listOf(),
                orderIsRandom = true,
                isRandomRules = true
            )

            (1..2).forEach { _ -> if (gameIsActive) rootService.easyBotService.takeTurn()}

        }
    }

    override fun refreshAfterGameEnd(scores: Map<String, ScoringService.Companion.PlayerScore>) {
        gameIsActive = false
    }
}