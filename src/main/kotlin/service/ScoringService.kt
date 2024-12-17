package service

import entity.HabitatTile
import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootService : RootService) : AbstractRefreshingService() {


    companion object {
        /**
         * The offsets and the corresponding Edge index of the neighbour
         */
        private val directionsPairsAndCorrespondingEdges: Map<Pair<Int, Int>, Int> =
            mapOf(
                Pair(-1, 1) to 3,
                Pair(0, 1) to 4,
                Pair(1, 0) to 5,
                Pair(1, -1) to 0,
                Pair(0, -1) to 1,
                Pair(-1, 0) to 2
            )

        /**
         * Adding a pair to another
         */
        private val addPairs: (Pair<Int, Int>, Pair<Int, Int>) -> Pair<Int, Int> = { a, b ->
            a.first + b.first to a.second + b.second
        }
        private val getNeighbours : (Pair<Int,Int>) -> List<Pair<Int,Int>> = {
            pair -> directionsPairsAndCorrespondingEdges.keys.map { addPairs(pair, it) }
        }
        private fun Pair<Int,Int>.neighbours () : List<Pair<Int,Int>>{
            return directionsPairsAndCorrespondingEdges.keys.map { addPairs(it,this) }
        }

        /**
         * Calculating the longest path starting at some coordinates
         * @param coordinate the start coordinate
         * @param graph the graph to search
         * @param visited the visited coordinates so far
         */
        private fun depthFirstConnectedComponentLength(graph: Map<Pair<Int,Int>, List<Pair<Int,Int>>>,
                                            visited : MutableSet<Pair<Int,Int>>,
                                            coordinate: Pair<Int, Int>) : Int {
            var connectedComponentLength : Int = 1
            visited.add(coordinate)
            val neighbours = coordinate.neighbours()

            for(neighbour in neighbours ){
                if(!visited.contains(neighbour))
                    connectedComponentLength+= depthFirstConnectedComponentLength(graph, visited, neighbour)
            }
            return connectedComponentLength

        }
    }
    /**
     *
     */
    fun calculateScore(player : Player) {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
    }

    /***
     * Calculating the longest connected terrains of some type for some player
     * @param type the wished [Terrain] type
     * @param player The [Player] having this longest terrains
     */
    private fun calculateLongestTerrain(searchedTerrain : Terrain, player : Player) : Int {

        val hasAtLeastOneEdgeOfSearchedTerrain : (HabitatTile) -> Boolean = {it.terrains.any { it==searchedTerrain }}
        val buildSearchedTerrainGraph : (Map<Pair<Int,Int>, HabitatTile>) -> Map<Pair<Int,Int>, List<Pair<Int,Int>>> ={
                playerTiles ->
            val searchedTerrainNodesCoordinates = playerTiles.
            filterValues { hasAtLeastOneEdgeOfSearchedTerrain(it) }
                .keys.toSet()

            val graph = searchedTerrainNodesCoordinates.associateWith { coordinate ->
                coordinate
                    .neighbours()
                    .filter { neighbour-> searchedTerrainNodesCoordinates.contains(neighbour) }
            }
            graph

        }
        val searchedTerrainGraph = buildSearchedTerrainGraph(player.habitat)
        val visited : MutableSet<Pair<Int,Int>> = mutableSetOf()
        var longestConnectedComponent = 0
        for(terrainNode in searchedTerrainGraph.keys){
            if(!visited.contains(terrainNode))
                longestConnectedComponent = maxOf(
                    longestConnectedComponent,
                    depthFirstConnectedComponentLength(searchedTerrainGraph, visited, terrainNode)
                )
        }
        return longestConnectedComponent
    }

    /**
     *
     */
    private fun calculateBearScore(player : Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateElkScore(player : Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateHawkScore(player : Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateSalmonScore(player : Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateFoxScore(player : Player) {
        //ToDo
    }

}