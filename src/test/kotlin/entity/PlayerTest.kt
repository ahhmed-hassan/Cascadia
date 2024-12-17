package entity

import kotlin.test.*

/**
 * [PlayerTest] class for the Player functionality.
 *
 * This class contains unit tests to validate the behavior of the `Player` class,
 * including property initialization, nature token updates, and score management.
 */
class PlayerTest {

    /**
     * Tests the creation and initialization of a Player object.
     *
     * Verifies:
     *      Player name is correctly assigned.
     *      Habitat map is initialized as expected.
     *      Player type is correctly set.
     *      Default values for nature tokens and score are zero.
     */
    @Test
    fun testPlayer() {
        val name = "Cascadia Player"
        val habitat = mapOf<Pair<Int,Int>, HabitatTile>()
        val playerType = PlayerType.LOCAL

        val player = Player(name, habitat, playerType)

        assertEquals(name, player.name)
        assertEquals(habitat, player.habitat)
        assertEquals(playerType, player.playerType)
        assertEquals(0, player.natureToken)
        assertEquals(0, player.score)
    }

    /**
     * Tests the incrementing of the natureToken property.
     *
     * Verifies:
     *      The natureToken count increases correctly.
     */
    @Test
    fun testNatureToken() {
        val player = Player("Test Player", emptyMap(), PlayerType.NETWORK)

        player.natureToken += 1

        assertEquals(1, player.natureToken)
    }

    /**
     * Tests the incrementing of the score property.
     *
     * Verifies:
     *      The score increases correctly after an update.
     */
    @Test
    fun testScore() {
        val player = Player("Test Player", emptyMap(), PlayerType.LOCAL)

        player.score += 64

        assertEquals(64, player.score)
    }
}