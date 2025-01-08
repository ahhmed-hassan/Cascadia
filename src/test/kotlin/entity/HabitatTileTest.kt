package entity

import kotlin.test.*

/**
 * [HabitatTileTest] class for the HabitatTile functionality.
 *
 * This class contains unit tests to verify the behavior of the `HabitatTile` class,
 * including validation of its properties, rotation offset, wildlife tokens, and terrain setup.
 */
class HabitatTileTest {

    /**
     * Tests the creation and initialization of a HabitatTile.
     *
     * Verifies:
     *      The ID is assigned correctly.
     *      Keystone tile property is correctly initialized.
     *      Wildlife symbols are added.
     *      Wildlife token is not null.
     *      Terrains are properly added to the HabitatTile.
     */
    @Test
    fun testHabitatTile() {
        val id = 0
        val isKeystoneTile = false
        val rotationOffset = 0

        val wildLifeSymbols = listOf(Animal.BEAR, Animal.ELK)
        val wildlifeToken = WildlifeToken(Animal.ELK)
        val terrains = mutableListOf(Terrain.FOREST, Terrain.MOUNTAIN)

        val habitatTile = HabitatTile(
            id,
            isKeystoneTile,
            rotationOffset,
            wildLifeSymbols,
            wildlifeToken,
            terrains
        )

        assertEquals(id, habitatTile.id)
        assertFalse(habitatTile.isKeystoneTile)
        assertEquals(rotationOffset, habitatTile.rotationOffset)
        assertEquals(wildLifeSymbols, habitatTile.wildlifeSymbols)
        assertNotNull(habitatTile.wildlifeToken)
        assertEquals(terrains, habitatTile.terrains)
    }

    /**
     * Tests the rotation offset functionality of a HabitatTile.
     *
     * Verifies:
     *      Rotation offset can be updated and accessed correctly.
     */
    @Test
    fun testRotation() {
        val habitatTile = HabitatTile(
            2,
            false,
            0,
            emptyList(),
            null,
            mutableListOf()
        )

        habitatTile.rotationOffset = 3

        assertEquals(3, habitatTile.rotationOffset)
    }

    /**
     * Tests adding a WildlifeToken to a HabitatTile.
     *
     * Verifies:
     *      The wildlife token is assigned correctly.
     *      The assigned wildlife token is not null.
     */
    @Test
    fun testAddWildlifeToken() {
        val habitatTile = HabitatTile(
            1,
            false,
            0,
            listOf(Animal.FOX),
            null,
            mutableListOf()
        )
        val wildlifeToken = WildlifeToken(Animal.FOX)

        habitatTile.wildlifeToken = wildlifeToken

        assertNotNull(habitatTile.wildlifeToken)
        assertEquals(wildlifeToken, habitatTile.wildlifeToken)
    }

    /**
     * Tests the creation and addition of terrains to a HabitatTile.
     *
     * Verifies:
     *      The correct number of terrains is added.
     *      The specified terrains are present in the HabitatTile.
     */
    @Test
    fun testCreateTerrain() {
        val terrains = mutableListOf(Terrain.RIVER, Terrain.WETLAND)

        val habitatTile = HabitatTile(
            3,
            false,
            0,
            emptyList(),
            null,
            terrains
        )

        assertEquals(2, habitatTile.terrains.size)
        assertTrue(habitatTile.terrains.contains(Terrain.RIVER))
        assertTrue(habitatTile.terrains.contains(Terrain.WETLAND))
    }
}