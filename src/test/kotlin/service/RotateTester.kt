package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType
import entity.Terrain
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Class for testing the [PlayerActionService.rotateTile] function
 */
class RotateTester {
    private var rootService = RootService()

    @BeforeTest
    fun setup() {
        rootService.gameService.startNewGame(
            mapOf("Ahmed" to PlayerType.NORMAL, "Hello" to PlayerType.NORMAL),
            listOf(true, true, true, true)
        )
    }

    @Test
    fun properRotation() {
        rootService.currentGame?.selectedTile = HabitatTile(
            73,
            false,
            0,
            wildlifeSymbols = mutableListOf(Animal.ELK, Animal.HAWK),
            wildlifeToken = null,
            terrains = mutableListOf(
                Terrain.PRAIRIE, Terrain.PRAIRIE, Terrain.PRAIRIE,
                Terrain.MOUNTAIN, Terrain.MOUNTAIN, Terrain.MOUNTAIN
            )
        )

        rootService.playerActionService.rotateTile()
        assertEquals(
            1, rootService.currentGame?.selectedTile?.rotationOffset,
            "Rotation offset not incremented as wanted"
        )
        val expectedList: MutableList<Terrain> = mutableListOf(
            Terrain.PRAIRIE, Terrain.PRAIRIE, Terrain.MOUNTAIN,
            Terrain.MOUNTAIN, Terrain.MOUNTAIN, Terrain.PRAIRIE
        )
        assertEquals(
            expectedList, rootService.currentGame?.selectedTile?.terrains,
            "The list elements are not rotated properly"
        )

        rootService.playerActionService.rotateTile()
        assertEquals(
            2, rootService.currentGame?.selectedTile?.rotationOffset,
            "Rotation offset not incremented as wanted"
        )
        val expectedListAfterTwoRotations = mutableListOf(
            Terrain.PRAIRIE, Terrain.MOUNTAIN, Terrain.MOUNTAIN,
            Terrain.MOUNTAIN, Terrain.PRAIRIE, Terrain.PRAIRIE
        )
        assertEquals(
            expectedListAfterTwoRotations, rootService.currentGame?.selectedTile?.terrains,
            "The list elements are not rotated properly"
        )
    }

    @Test
    fun rotatingNullTile() {
        val exception = assertThrows<IllegalArgumentException> { rootService.playerActionService.rotateTile() }
        assertEquals("Only the selected tile can be rotated!", exception.message)
    }
}