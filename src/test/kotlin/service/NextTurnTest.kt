package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType
import entity.WildlifeToken
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * This test class validates the functionality of [service.GameService.nextTurn], ensuring that the game
 * properly handles the end of the current player's turn and sets up the game state for the next player.
 */
class NextTurnTest {
    /**
     * Tests the behavior of the [service.GameService.nextTurn] method from the [GameService] class.
     */
    @Test
    fun testNextTurn() {
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

        //Test: Check if the current player can proceed to the next turn without performing an action
        assertThrows<IllegalStateException> { gameServ.nextTurn() }

        //Test: Check if shop gets refilled (Token-Tile Pair)
        playerActionServ.chooseTokenTilePair(0)
        assertEquals(null, game.shop[0].first)
        assertEquals(null, game.shop[0].second)

        game.hasPlayedTile = true
        game.selectedToken = null
        game.selectedTile = null

        gameServ.nextTurn()
        assert(game.shop[0].first != null)
        assert(game.shop[0].second != null)

        //Test: Check if shop gets refilled (Custom Pair)
        game.currentPlayer.natureToken += 1
        playerActionServ.chooseCustomPair(0, 1)
        assertEquals(null, game.shop[0].first)
        assertEquals(null, game.shop[1].second)

        game.hasPlayedTile = true
        game.selectedToken = null
        game.selectedTile = null

        gameServ.nextTurn()
        assertNotNull(game.shop[0].first)
        assertNotNull(game.shop[1].second)

        //Test: Check if overpopulation is resolved
        val habitatTile = HabitatTile(
            1,
            false,
            0,
            listOf(Animal.FOX),
            null,
            mutableListOf()
        )

        game.shop[0] = Pair(habitatTile, WildlifeToken(Animal.FOX))
        game.shop[1] = Pair(habitatTile, WildlifeToken(Animal.FOX))
        game.shop[2] = Pair(habitatTile, WildlifeToken(Animal.FOX))
        game.shop[3] = Pair(habitatTile, WildlifeToken(Animal.FOX))

        assertTrue(gameServ.checkForSameAnimal())

        game.hasPlayedTile = true

        gameServ.nextTurn()

        assertEquals(false, gameServ.checkForSameAnimal())

        //Test: Check if current player gets switched
        val currentPLayer = game.currentPlayer

        game.hasPlayedTile = true
        game.selectedToken = null
        game.selectedTile = null

        assertEquals(currentPLayer, game.currentPlayer)

        gameServ.nextTurn()

        assertNotEquals(currentPLayer, game.currentPlayer)
    }
}