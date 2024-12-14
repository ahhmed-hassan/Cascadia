package service

import entity.Animal
import entity.HabitatTile
import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootSerivce : RootService) : AbstractRefreshingService() {


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

        /**
         * Calculating the longest path starting at some coordinates
         * @param coordinate the start coordinate
         * @param graph the graph to search
         * @param visited the visited coordinates so far
         */
        private fun depthFirstLongestPathAt(graph: Map<Pair<Int,Int>, List<Pair<Int,Int>>>,
                                            visited : MutableSet<Pair<Int,Int>>,
                                            coordinate: Pair<Int, Int>) : Int {
            var longestPath : Int = 1
            visited.add(coordinate)
            val notVisitedNeighbours =  directionsPairsAndCorrespondingEdges.keys.map { addPairs(it,coordinate) }
                .filter { neighbour -> !visited.contains(neighbour) }
            for(notVisitedNeighbour in notVisitedNeighbours ){
                longestPath+= depthFirstLongestPathAt(graph, visited, notVisitedNeighbour)
            }
            return longestPath

        }
    }
    /**
     *
     */
    fun calculateScore(player : Player) {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
    }

    /**
     *
     */
    private fun calculateLongestTerrain(type : Terrain, player : Player) {
        //ToDo
    }

    /**
     *Calculates the Score resulted from the bear collection
     * @param player the player whose score should be calculated
     */
    private fun calculateBearScore(player : Player): Int  {
        val makeBearGraph : (Map<Pair<Int,Int>, HabitatTile>) -> Map<Pair<Int,Int>, List<Pair<Int,Int>>> ={
                habitatTiles ->
            val bearNodesCoordinates=  habitatTiles.filterValues { it.wildlifeToken?.animal == Animal.BEAR }
                .keys.toSet()

            val graph = bearNodesCoordinates.associateWith { coordinate ->
                directionsPairsAndCorrespondingEdges.keys
                    .map { addPairs(it, coordinate) }
                    .filter { neighbour -> bearNodesCoordinates.contains(neighbour) }

            }
            graph
        }
        val bearGraph = makeBearGraph(player.habitat)
        val visited : MutableSet<Pair<Int,Int>> = mutableSetOf()
        val isB = rootSerivce.currentGame!!.ruleSet[Animal.BEAR.ordinal]
        val searchedLength = if (isB) 3 else 2
        var connectedComponentsWithSearchedLength = 0

        for(bearNode in bearGraph.keys){
            if(!visited.contains(bearNode)) {
                if (depthFirstLongestPathAt(bearGraph, visited, bearNode) == searchedLength) {
                    connectedComponentsWithSearchedLength++
                }
            }
        }
        if(isB)
            return 10 * connectedComponentsWithSearchedLength
        else
            return when (connectedComponentsWithSearchedLength){
                1 -> 4
                2 -> 11
                3 -> 19
                4 -> 27
                else -> 0
            }
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