package service

import entity.PlayerType
import kotlin.test.Test

class HardBotTest {

    @Test
    fun hardBotTest() {
        val rootService = RootService()

        val players = mapOf("A" to PlayerType.NORMAL, "B" to PlayerType.NORMAL)

        rootService.gameService.startNewGame(
            playerNames = players,
            scoreRules = listOf(),
            orderIsRandom = true,
            isRandomRules = true
        )

        println("start")
        rootService.hardBotService.takeTurn()
        println("finish")
    }
}