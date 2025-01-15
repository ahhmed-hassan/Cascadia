package service

import entity.HabitatTile
import entity.Player
import entity.PlayerType
import entity.Terrain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A testing class that validates the functionality of the method [ScoringService.calculateLongestTerrain]
 * a player in the Cascadia board game. The tests include specific scenarios
 * as outlined in the game's ruleset.
 * @property player some dummy player to be shared among different tests
 * @property rootService the [RootService] to access the [ScoringService]
 */
class LongestTerrainTester {
    val player: Player = Player("dummy", mutableMapOf(), PlayerType.NORMAL)
    val rootService = RootService()

    /**
     * Helper function for make a dummy [HabitatTile] that focuses only on the right order of the terrains
     * @param terrainsList the wished order of [Terrain]s
     * */
    private fun dummyTile(terrainsList: List<Terrain>): HabitatTile {
        check(terrainsList.size == 6)
        check(terrainsList.distinct().size in 1..2)
        return HabitatTile(
            id = Int.MAX_VALUE /*Not important*/,
            isKeystoneTile = false /*Not important*/,
            rotationOffset = Int.MAX_VALUE /*Not important*/,
            wildlifeSymbols = listOf() /*Not important*/,
            wildlifeToken = null /*Not important*/,
            terrains = terrainsList.toMutableList()
        )
    }

    /**
     * Enabling [Terrain] + List<[Terrain]> operation
     */
    private operator fun Terrain.plus(other: List<Terrain>): MutableList<Terrain> =
        (mutableListOf(this) + other).toMutableList()

    /**
     * Enabling Terrain + Terrain
     * @return [List] containing both terrains
     */
    private operator fun Terrain.plus(other: Terrain): MutableList<Terrain> = mutableListOf(this, other)

    /**
     * Setting up the tiles as stated in page 9 of ruleset
     */
    @BeforeTest
    fun setup() {
        val tilesAndCoordinates: Map<Pair<Int, Int>, HabitatTile> = mapOf(
            (-2 to -1) to dummyTile(
                Terrain.PRAIRIE.plus(List(3) { Terrain.RIVER }) +  List(2) { Terrain.PRAIRIE }),
            (-2 to 0) to dummyTile(
                List(3) { Terrain.PRAIRIE } + List(3) { Terrain.FOREST }),
            /*second Row*/
            (-1 to -2) to dummyTile(
                Terrain.RIVER + List(3) { Terrain.PRAIRIE } + Terrain.RIVER.plus(Terrain.RIVER)),
            (-1 to -1) to dummyTile(
                List(2) { Terrain.RIVER } + List(3) { Terrain.PRAIRIE } + Terrain.RIVER),
            (-1 to 0) to dummyTile(
                List(6) { Terrain.RIVER }),
            (-1 to 1) to dummyTile(
                List(3) { Terrain.MOUNTAIN } + List(3) { Terrain.RIVER }),
            (-1 to 2) to dummyTile(
                Terrain.RIVER + List(3) { Terrain.MOUNTAIN } + Terrain.RIVER + Terrain.RIVER),
            (-1 to 3) to dummyTile(
                List(2) { Terrain.RIVER } + List(3) { Terrain.MOUNTAIN } + Terrain.RIVER),
            /*third row*/
            (0 to -3) to dummyTile(
                List(3) { Terrain.PRAIRIE } + List(3) { Terrain.WETLAND }),
            (0 to -2) to dummyTile(
                List(2) { Terrain.PRAIRIE } + List(3) { Terrain.MOUNTAIN } + Terrain.PRAIRIE),
            (0 to -1) to dummyTile(
                Terrain.PRAIRIE + List(3) { Terrain.FOREST } + List(2) { Terrain.PRAIRIE }),
            (0 to 0) to dummyTile(
                List(2) { Terrain.MOUNTAIN } + List(3) { Terrain.WETLAND } + Terrain.MOUNTAIN),
            (0 to 1) to dummyTile(
                Terrain.MOUNTAIN + List(3) { Terrain.WETLAND } + List(2) { Terrain.MOUNTAIN }),
            (0 to 2) to dummyTile(
                List(2) { Terrain.MOUNTAIN } + List(3) { Terrain.WETLAND } + Terrain.MOUNTAIN),
            /*4th row*/
            (1 to -3) to dummyTile(
                List(6) { Terrain.PRAIRIE }),
            (1 to -2) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.PRAIRIE }),
            (1 to -1) to dummyTile(
                List(3) { Terrain.WETLAND } + List(3) { Terrain.FOREST }),
            (1 to 0) to dummyTile(
                Terrain.WETLAND + List(3) { Terrain.FOREST } + List(2) { Terrain.WETLAND }),
            (1 to 1) to dummyTile(
                List(3) { Terrain.WETLAND } + List(3) { Terrain.FOREST }),
            (1 to 2) to dummyTile(
                List(6) { Terrain.WETLAND }),
            /*5th row*/
            (2 to -2) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.PRAIRIE }),
            (2 to -1) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.WETLAND }),
            (2 to 0) to dummyTile(
                List(6) { Terrain.FOREST })
        )
        player.habitat.putAll(tilesAndCoordinates)
    }

    /**
     * Calculate the longest [Terrain.MOUNTAIN] path for [player] member
     */
    @Test
    fun testLongestMountainPath() {
        val longestMountainPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.MOUNTAIN, player.habitat
        )
        assertEquals(6, longestMountainPath)
    }

    /**
     *Calculate the longest [Terrain.FOREST] path for [player] member
     */
    @Test
    fun testLongestForestPath() {
        val longestForestPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.FOREST, player.habitat
        )
        assertEquals(4, longestForestPath)
    }

    /**
     *Calculate the longest [Terrain.PRAIRIE] path for [player] member
     */
    @Test
    fun testLongestPrairiePath() {
        val longestPrairiePath = rootService.scoringService.calculateLongestTerrain(
            Terrain.PRAIRIE, player.habitat
        )
        assertEquals(7, longestPrairiePath)
    }

    /**
     *Calculate the longest [Terrain.RIVER] path for [player] member
     */
    @Test
    fun testLongestRiverPath() {
        val longestRiverPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.RIVER, player.habitat
        )
        assertEquals(5, longestRiverPath)
    }

    /**
     *Calculate the longest [Terrain.WETLAND] path for [player] member
     */
    @Test
    fun testLongestWetlandPath() {
        val longestWetlandPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.WETLAND, player.habitat
        )
        assertEquals(8, longestWetlandPath)
    }

    /**
     * Checking that longest Wetland path for player with no Wet lands is 0
     */
    @Test
    fun testNoWetLands() {
        val player = Player(
            "dummy",
            mutableMapOf(
                (0 to 0) to dummyTile(
                    List(6) { Terrain.RIVER }
                ),
                (0 to 1) to dummyTile(
                    List(6) { Terrain.MOUNTAIN }
                ),
                (0 to 3) to dummyTile(
                    List(6) { Terrain.RIVER }
                )
            ),
            PlayerType.NORMAL)
        assertEquals(0, rootService.scoringService.calculateLongestTerrain(Terrain.WETLAND, player.habitat))
    }
}