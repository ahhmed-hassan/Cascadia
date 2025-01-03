package service

import entity.*
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
        val playerActionServ = PlayerActionService(rootServ)
        gameServ.startNewGame(mapOf("Alice" to PlayerType.LOCAL), listOf(true, false, true, false, true))
        val game = rootServ.currentGame
        checkNotNull(game)
        val currentPlayer = game.currentPlayer

        //Test: Check if customPair can be selected if current player has no nature tokens
        assertThrows<IllegalArgumentException> { rootServ.playerActionService.chooseCustomPair(0, 1) }

        game.selectedTile = HabitatTile(
            0,
            false,
            0,
            listOf(Animal.FOX),
            null, mutableListOf(Terrain.FOREST)
        )
        game.selectedToken = WildlifeToken(Animal.FOX)

        currentPlayer.natureToken.inc()
        //Test: Check if the player can select a custom pair when already selected a pair
        assertThrows<IllegalArgumentException> { rootServ.playerActionService.chooseCustomPair(0, 2) }

        game.selectedTile = null
        game.selectedToken = null

        //Test; Check if the player can select a pair that does not exist
        assertThrows<IllegalArgumentException>({
            rootServ.playerActionService.chooseCustomPair(
                4,
                4
            )
        }
        )

        assertEquals(null, game.selectedTile)
        assertEquals(null, game.selectedToken)
        assertEquals(1, currentPlayer.natureToken)

        val chosenTile = game.shop[0].first
        val chosenToken = game.shop[2].second

        rootServ.playerActionService.chooseCustomPair(0, 2)

        assertEquals(chosenTile, game.selectedTile)
        assertEquals(chosenToken, game.selectedToken)
        assertEquals(0, currentPlayer.natureToken)
    }
}