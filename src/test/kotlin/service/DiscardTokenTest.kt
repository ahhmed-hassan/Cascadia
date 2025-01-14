package service

import entity.Animal
import entity.PlayerType
import entity.WildlifeToken
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test class verifies the functionality of discarding a selected token in the game.
 */
class DiscardTokenTest {
    /**
     * Tests the behavior of the [service.PlayerActionService.discardToken] method from the [PlayerActionService] class.
     */
    @Test
    fun testDiscardToken() {
        // Preparations for the tests
        val rootServ = RootService()
        val gameServ = GameService(rootServ)
        val playerActionServ = PlayerActionService(rootServ)
        gameServ.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL, "Bob" to PlayerType.EASY), listOf(true, false, true, false, true),
            false, isRandomRules = false, startTileOrder = null
        )
        val game = rootServ.currentGame
        checkNotNull(game)

        rootServ.networkService.connectionState = ConnectionState.PLAYING_MY_TURN

        assertEquals(null, game.selectedToken)

        game.selectedToken = WildlifeToken(Animal.FOX)
        game.hasPlayedTile = true

        val selectedToken = game.selectedToken
        checkNotNull(selectedToken)

        assertEquals(Animal.FOX, selectedToken.animal)

        playerActionServ.discardToken()

        assertEquals(null, game.selectedToken)


    }
}