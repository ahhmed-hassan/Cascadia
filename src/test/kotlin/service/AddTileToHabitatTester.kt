package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType
import entity.Terrain
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.test.*

/**
 * A class to test the service method [PlayerActionService.addTileToHabitat]
 * @property dummyHabitat just a random [HabitatTile]
 */
class AddTileToHabitatTester {
    private val rootService = RootService()

    private val dummyHabitat =
        HabitatTile(
            id = Random.nextInt(),
            isKeystoneTile = Random.nextBoolean(),
            rotationOffset = Random.nextInt(),
            wildlifeSymbols = listOf(Animal.values().random()),
            wildlifeToken = null,
            terrains = List(6) { Terrain.values().random() }.toMutableList()
        )


    /**
     * Setting up the player starter tiles with the stater coordinates and putting
     * [entity.CascadiaGame.selectedTile] to some value
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startNewGame(
            mapOf("FirstBlahBlah" to PlayerType.LOCAL, "secondBlahBlah" to PlayerType.EASY),
            List(5) { true },
            false,
            false,
        )
        val game = checkNotNull(rootService.currentGame)
        game.selectedTile = dummyHabitat
        val currentPlayer = game.currentPlayer
        currentPlayer.habitat.clear()
        /*starter Tiles
        *  X    0,0
        * X X*/
        currentPlayer.habitat.putAll((listOf(0 to 0, 1 to -1, 1 to 0).associateWith { dummyHabitat }))


    }

    /**
     * Putting the selected tile not neighbouring to any already put tiles
     */
    @Test
    fun notNeighbour() {
        val notANeighbour = assertThrows<IllegalArgumentException> {
            rootService.playerActionService.addTileToHabitat(-2 to 0)
        }
        assertEquals("A habitat tile shall only be placed to an already placed one", notANeighbour.message)
        assertNotNull(checkNotNull(rootService.currentGame).selectedTile)
    }

    /**
     * Trying to put the [entity.CascadiaGame.selectedTile] on a place that already has a [HabitatTile] on it
     */
    @Test
    fun existingCoordinates() {
        val alreadyPlaced = assertThrows<IllegalArgumentException> {
            rootService.playerActionService.addTileToHabitat(0 to 0)
        }
        assertEquals("At this coordinate there is already an existing tile", alreadyPlaced.message)
        assertNotNull(checkNotNull(rootService.currentGame).selectedTile)
    }

    /**
     * Trying to put nothing (the selected token is null)
     */
    @Test
    fun noSelectedToken() {
        checkNotNull(rootService.currentGame).selectedTile = null
        val noTokenSelected = assertThrows<IllegalStateException> {
            rootService.playerActionService.addTileToHabitat(-1 to 0)
        }
        assertEquals("No habitat tile has been chosen yet", noTokenSelected.message)
        assertNull(checkNotNull(rootService.currentGame).selectedTile)
    }

    /**
     * The happy path!
     */
    @Test
    fun validPlace() {
        assertDoesNotThrow { rootService.playerActionService.addTileToHabitat(0 to -1) }
    }
}