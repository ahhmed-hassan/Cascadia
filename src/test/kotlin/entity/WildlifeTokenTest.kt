package entity

import kotlin.test.*

/**
 * [WildlifeTokenTest] class for the WildlifeToken functionality.
 *
 * This class contains a unit test to verify the behavior and initialization of the `WildlifeToken` class.
 */
class WildlifeTokenTest {

    /**
     * Tests the creation and initialization of a WildlifeToken object.
     *
     * Verifies:
     *      The `WildlifeToken` object is not null upon creation.
     *      The associated animal type is correctly assigned.
     */
    @Test
    fun testWildlifeToken() {
        val animal = Animal.FOX

        val wildlifeToken = WildlifeToken(animal)

        assertNotNull(wildlifeToken)
        assertEquals(Animal.FOX, wildlifeToken.animal)
    }
}