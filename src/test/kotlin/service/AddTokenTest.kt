package service

import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test class verifies the functionality of adding a selected token to a tile in the game.
 */
class AddTokenTest {
    /**
     * Tests the behavior of the [service.PlayerActionService.addToken] method from the [PlayerActionService] class.
     */
    @Test
    fun testAddToken() {
        // Preparations for the tests
        val rootServ = RootService()
        val gameServ = GameService(rootServ)
        val playerActionServ = PlayerActionService(rootServ)
        gameServ.startNewGame(mapOf("Alice" to PlayerType.LOCAL), listOf(true, false, true, false, true))
        val game = rootServ.currentGame
        checkNotNull(game)
        val currentPlayer = game.currentPlayer

        game.selectedToken = WildlifeToken(Animal.FOX)

        val tile1 = HabitatTile(
            1, false, 0, listOf(Animal.FOX), WildlifeToken(Animal.FOX),
            mutableListOf(Terrain.FOREST)
        )

        val tile2 = HabitatTile(
            2, false, 0, listOf(Animal.BEAR), null,
            mutableListOf(Terrain.FOREST)
        )

        val tile3 = HabitatTile(
            3, true, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.FOREST)
        )

        assertEquals(0, currentPlayer.natureToken)

        assertThrows<IllegalArgumentException> { playerActionServ.addToken(tile1) }

        assertThrows<IllegalArgumentException> { playerActionServ.addToken(tile2) }

        playerActionServ.addToken(tile3)

        assertEquals(1, currentPlayer.natureToken)

        assertEquals(null, game.selectedToken)

    }
}