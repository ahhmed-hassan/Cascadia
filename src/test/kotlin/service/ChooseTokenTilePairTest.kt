package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType
import entity.WildlifeToken
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * This test class verifies the functionality of choosing a token-tile pair from the shop in the game.
 */
class ChooseTokenTilePairTest {
    /**
     * Tests the behavior of the [service.PlayerActionService.chooseTokenTilePair] method from the
     * [PlayerActionService] class.
     */
    fun testChooseTokenTilePair() {
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

        //Test: Check if the player can choose a pair outside the shop boundaries
        assertThrows<IllegalStateException> { rootServ.playerActionService.chooseTokenTilePair(4) }

        game.selectedToken = WildlifeToken(Animal.FOX)
        game.selectedTile = HabitatTile(
            1,
            false,
            0,
            listOf(Animal.FOX),
            null,
            mutableListOf()
        )

        //Test: Check if the player can select a pair of the shop if they already selected a pair
        assertThrows<IllegalStateException> { rootServ.playerActionService.chooseTokenTilePair(3) }

        game.selectedToken = null
        game.selectedTile = null

        //Test: Check if the chosen pair is set to null after being selected
        val selectedToken = game.shop[3].second
        val selectedTile = game.shop[3].first

        rootServ.playerActionService.chooseTokenTilePair(3)

        assertEquals(selectedToken, game.selectedToken)
        assertEquals(selectedTile, game.selectedTile)

        assertNull(game.shop[3].first)
        assertNull(game.shop[3].second)
    }

}