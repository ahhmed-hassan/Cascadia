package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * A test class for the CascadiaGame class.
 */
class CascadiaGameTest {
    /**
     * Tests the initialization of a [entity.CascadiaGame] instance with given parameters.
     */
    @Test
    fun testCascadiaGame() {

        // Preparations for testing
        val testTile = HabitatTile(
            1, false, 0, listOf(Animal.BEAR), null,
            mutableListOf(Terrain.FOREST)
        )

        val testToken = WildlifeToken(Animal.BEAR)

        val habitatMap: MutableMap<Pair<Int, Int>, HabitatTile> = mutableMapOf()
        val testPlayer = Player("Alice", habitatMap, PlayerType.LOCAL)

        val startTileList = listOf(listOf(testTile, testTile))

        val ruleSet = listOf(false, true, false, true, false)

        val shop = mutableListOf(
            Pair(testTile, testToken),
            Pair(null, null)
        )
        val discardedTokens = mutableListOf<WildlifeToken?>()
        val habitatTileList = mutableListOf(testTile)
        val wildlifeTokenList = mutableListOf(testToken)

        // initializes a game instance with given parameters from above
        val game = CascadiaGame(
            startTileList = startTileList,
            ruleSet = ruleSet,
            simulationSpeed = 1.0f,
            natureToken = 5,
            hasReplacedThreeToken = false,
            hasPlayedTile = false,
            shop = shop,
            discardedToken = discardedTokens,
            currentPlayer = testPlayer,
            playerList = listOf(testPlayer),
            habitatTileList = habitatTileList,
            selectedTile = null,
            selectedToken = null,
            wildlifeTokenList = wildlifeTokenList
        )

        //Test: Check if start tiles list has been correctly
        assertEquals(startTileList, game.startTileList)

        //Test: Check if rule set has been
        assertEquals(listOf(false, true, false, true, false), game.ruleSet)

        //Test: Check if simulation speed of game is 1.0f
        assertEquals(1.0f, game.simulationSpeed)

        //Test: Check if the game initiates with 5 nature tokens
        assertEquals(5, game.natureToken)

        //Test: Check if hasReplacedThreeToken is false
        assertFalse(game.hasReplacedThreeToken)

        //Test: Check if hasPlayedTile is false
        assertFalse(game.hasPlayedTile)

        //Test: Check if game shop has been
        assertEquals(shop, game.shop)

        //Test: Check if discard Tokens has been
        assertEquals(discardedTokens, game.discardedToken)

        //Test: Check if testPlayer is currentPlayer
        assertEquals(testPlayer, game.currentPlayer)

        //Test: Check if the given list is the game player list
        assertEquals(listOf(testPlayer), game.playerList)

        //Test: Check if the given mutable list is the games habitat tile list
        assertEquals(mutableListOf(testTile), game.habitatTileList)

        //Test: Check if a tile has been selected
        assertEquals(null, game.selectedTile)

        //Test: Check if a toke has been selected
        assertEquals(null, game.selectedToken)

        //Test: Check if the mutable list is the games wildlife token list
        assertEquals(wildlifeTokenList, game.wildlifeTokenList)
    }
}
