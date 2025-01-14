package service

import entity.PlayerType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * This test class verifies the functionality of choosing a custom pair from the shop in the game.
 */
class ChooseCustomPairTest {
    /**
     * Tests the behavior of the [service.PlayerActionService.chooseCustomPair] method from the
     * [PlayerActionService] class.
     */
    @Test
    fun testChooseCustomPair() {
        // Preparations for the tests
        val rootServ = RootService()
        val gameServ = GameService(rootServ)

        gameServ.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL, "Bob" to PlayerType.EASY),
            listOf(true, false, true, false, true),
            false,
            isRandomRules = false,
            startTileOrder = null
        )

        val game = rootServ.currentGame
        checkNotNull(game)
        val currentPlayer = game.currentPlayer

        //Test: Check if customPair can be selected if current player has no nature tokens
        assertThrows<IllegalStateException> { rootServ.playerActionService.chooseCustomPair(0, 1) }

        //Test; Check if the player can select a pair that does not exist
        assertThrows<IllegalStateException> {
            rootServ.playerActionService.chooseCustomPair(
                4,
                4
            )
        }

        currentPlayer.natureToken += 1


        assertEquals(null, game.selectedTile)
        assertEquals(null, game.selectedToken)
        assertEquals(false, game.hasPlayedTile)
        assertEquals(1, currentPlayer.natureToken)
        

        val chosenTile = game.shop[0].first
        val chosenToken = game.shop[2].second


        rootServ.playerActionService.chooseCustomPair(0, 2)

        assertEquals(chosenTile, game.selectedTile)
        assertEquals(chosenToken, game.selectedToken)
        assertEquals(0, currentPlayer.natureToken)

        //Test: Check if the player can choose a pair twice
        assertThrows<IllegalStateException> { rootServ.playerActionService.chooseCustomPair(0, 1) }
    }
}