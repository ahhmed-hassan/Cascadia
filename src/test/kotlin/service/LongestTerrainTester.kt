package service

import entity.HabitatTile
import entity.Player
import entity.PlayerType
import entity.Terrain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LongestTerrainTester {
    val player: Player = Player("dummy", mutableMapOf(), PlayerType.NORMAL)
    val rootService = RootService()
    private fun dummyTile(vararg terrainsArgs: Terrain): HabitatTile {
        check(terrainsArgs.size == 6)
        check(terrainsArgs.distinct().size in 1..2)
        return HabitatTile(
            id = Int.MAX_VALUE /*Not important*/,
            isKeystoneTile = false /*Not important*/,
            rotationOffset = Int.MAX_VALUE /*Not important*/,
            wildlifeSymbols = listOf() /*Not important*/,
            wildlifeToken = null /*Not important*/,
            terrains = terrainsArgs.toList().toMutableList()
        )
    }

    private fun dummyTile(terrainsList: List<Terrain>) = dummyTile(*terrainsList.toTypedArray())
    private operator fun Terrain.plus(other: List<Terrain>): MutableList<Terrain> =
        (mutableListOf(this) + other).toMutableList()

    private operator fun Terrain.plus(other: Terrain): MutableList<Terrain> = mutableListOf(this, other)


    @BeforeTest
    fun setup() {
        /*The setup environment in Cascadia Ruleset Page 9*/
        val tilesAndCoordinates: Map<Pair<Int, Int>, HabitatTile> = mapOf(
            (-2 to -1) to dummyTile(
                Terrain.PRAIRIE + List(3) { Terrain.RIVER } + List(2) { Terrain.PRAIRIE }
            ),
            (-2 to 0) to dummyTile(
                List(3) { Terrain.PRAIRIE } + List(3) { Terrain.FOREST }
            ),
            /*second Row*/
            (-1 to -2) to dummyTile(
                Terrain.RIVER + List(3) { Terrain.PRAIRIE } + Terrain.RIVER + Terrain.RIVER
            ),
            (-1 to -1) to dummyTile(
                List(2) { Terrain.RIVER } + List(3) { Terrain.PRAIRIE } + Terrain.RIVER
            ),
            (-1 to 0) to dummyTile(
                List(6) { Terrain.RIVER }
            ),
            (-1 to 1) to dummyTile(
                List(3) { Terrain.MOUNTAIN } + List(3) { Terrain.RIVER }
            ),
            (-1 to 2) to dummyTile(
                Terrain.RIVER + List(3) { Terrain.MOUNTAIN } + Terrain.RIVER + Terrain.RIVER
            ),
            (-1 to 3) to dummyTile(
                List(2) { Terrain.RIVER } + List(3) { Terrain.MOUNTAIN } + Terrain.RIVER
            ),
            /*third row*/
            (0 to -3) to dummyTile(
                List(3) { Terrain.PRAIRIE } + List(3) { Terrain.WETLAND }
            ),
            (0 to -2) to dummyTile(
                List(2) { Terrain.PRAIRIE } + List(3) { Terrain.MOUNTAIN } + Terrain.PRAIRIE
            ),
            (0 to -1) to dummyTile(
                Terrain.PRAIRIE + List(3) { Terrain.FOREST } + List(2) { Terrain.PRAIRIE }
            ),
            (0 to 0) to dummyTile(
                List(2) { Terrain.MOUNTAIN } + List(3) { Terrain.WETLAND } + Terrain.MOUNTAIN
            ),
            (0 to 1) to dummyTile(
                Terrain.MOUNTAIN + List(3) { Terrain.WETLAND } + List(2) { Terrain.MOUNTAIN }
            ),
            (0 to 2) to dummyTile(
                List(2) { Terrain.MOUNTAIN } + List(3) { Terrain.WETLAND } + Terrain.MOUNTAIN
            ),
            /*4th row*/
            (1 to -3) to dummyTile(
                List(6) { Terrain.PRAIRIE }
            ),
            (1 to -2) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.PRAIRIE }
            ),
            (1 to -1) to dummyTile(
                List(3) { Terrain.WETLAND } + List(3) { Terrain.FOREST }
            ),
            (1 to 0) to dummyTile(
                Terrain.WETLAND + List(3) { Terrain.FOREST } + List(2) { Terrain.WETLAND }
            ),
            (1 to 1) to dummyTile(
                List(3) { Terrain.WETLAND } + List(3) { Terrain.FOREST }
            ),
            (1 to 2) to dummyTile(
                List(6) { Terrain.WETLAND }
            ),
            /*5th row*/
            (2 to -2) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.PRAIRIE }
            ),
            (2 to -1) to dummyTile(
                List(3) { Terrain.FOREST } + List(3) { Terrain.WETLAND }
            ),
            (2 to 0) to dummyTile(
                List(6) { Terrain.FOREST }
            )


        )
        player.habitat.putAll(tilesAndCoordinates)
    }

    @Test
    fun testLongestPath() {
        val longestMountainPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.MOUNTAIN, player
        )
        val longestForestPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.FOREST, player
        )
        val longestWetlandPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.WETLAND, player
        )
        val longestPrairiePath = rootService.scoringService.calculateLongestTerrain(
            Terrain.PRAIRIE, player
        )
        val longestRiverPath = rootService.scoringService.calculateLongestTerrain(
            Terrain.RIVER, player
        )
        assertEquals(6, longestMountainPath)
        assertEquals(4, longestForestPath)
        assertEquals(8, longestWetlandPath)
        assertEquals(7, longestPrairiePath)
        assertEquals(5, longestRiverPath)
    }
}