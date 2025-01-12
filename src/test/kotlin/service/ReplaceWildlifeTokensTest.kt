package service

import entity.Animal
import entity.PlayerType
import entity.WildlifeToken
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * This test class validates the functionality of [service.PlayerActionService.replaceWildlifeTokens], ensuring that
 * the game properly handles the replacement of wildlife tokens from the shop.
 */
class ReplaceWildlifeTokensTest {
    /**
     * Tests the behavior of the [service.PlayerActionService.replaceWildlifeTokens] method from the
     * [PlayerActionService] class.
     */
    @Test
    fun testReplaceWildlifeToken() {
        // Preparations for the tests
        val rootServ = RootService()
        val gameServ = GameService(rootServ)
        gameServ.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL, "Bob" to PlayerType.EASY), listOf(true, false, true, false, true),
            false, isRandomRules = false, startTileOrder = null
        )
        val game = rootServ.currentGame
        checkNotNull(game)

        //Test: Check if the player can choose more than 4 token to replace
        assertThrows<IllegalArgumentException> {
            rootServ.playerActionService.replaceWildlifeTokens(
                listOf(0, 1, 2, 3, 4)
            )
        }

        //Test: Check if the player can put the same token index more than once as an argument
        assertThrows<IllegalArgumentException> {
            rootServ.playerActionService.replaceWildlifeTokens(
                listOf(
                    0, 0, 1, 2
                )
            )
        }

        //Test: Check if the player an index that is out of range e.g. 4
        assertThrows<IllegalArgumentException> {
            rootServ.playerActionService.replaceWildlifeTokens(
                listOf(0, 1, 4)
            )
        }

        //Test: Check if game can recognize that the wildlife token bag has not enough wildlife tokens for a replacement
        val tempWildlifeTokenList = game.wildlifeTokenList.toMutableList()

        val tokenSize = game.wildlifeTokenList.size
        for (i in tokenSize - 1 downTo 1) {
            game.wildlifeTokenList.removeAt(i)
        }

        assertThrows<IllegalStateException> {
            rootServ.playerActionService.replaceWildlifeTokens(
                listOf(0, 1, 2, 3)
            )
        }

        game.wildlifeTokenList = tempWildlifeTokenList

        //Test: Check if player can resolve an overpopulation of 3
        game.shop[0] = Pair(game.shop[0].first, WildlifeToken(Animal.FOX))
        game.shop[1] = Pair(game.shop[1].first, WildlifeToken(Animal.FOX))
        game.shop[2] = Pair(game.shop[2].first, WildlifeToken(Animal.FOX))

        assertTrue(rootServ.gameService.checkForSameAnimal(listOf(0, 1, 2)))
        assertFalse(game.hasReplacedThreeToken)

        rootServ.playerActionService.replaceWildlifeTokens(listOf(0, 1, 2))

        assertFalse(rootServ.gameService.checkForSameAnimal(listOf(0, 1, 2)))
        assertTrue(game.hasReplacedThreeToken)

        //Test: Check if any wildlife tokens can be replaced if the player has no nature token
        assertThrows<IllegalStateException> { rootServ.playerActionService.replaceWildlifeTokens(listOf(0, 1)) }

        //Test: Check if any wildlife tokens can be replaced if the player has a nature token
        game.currentPlayer.natureToken += 1

        rootServ.playerActionService.replaceWildlifeTokens(listOf(0))

        assertEquals(0, game.currentPlayer.natureToken)
    }
}