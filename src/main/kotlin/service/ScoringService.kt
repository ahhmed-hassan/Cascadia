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
     * Calculating the scores for the salmon runs
     * @param player the [Player] to calculate its runs.
     */
    private fun calculateSalmonScore(player : Player) : Int  {
        val hasSalmonToken : (HabitatTile) -> Boolean = {it.wildlifeToken?.animal == Animal.SALMON}
        val makeSalmonGraph : (Map<Pair<Int,Int>, HabitatTile>) -> Map<Pair<Int,Int>, List<Pair<Int,Int>>> = {
                habitatTile ->
            val salmonCoordinates =habitatTile.filterValues { hasSalmonToken(it) }.keys.toSet()
            val graph = salmonCoordinates.associateWith { coordinate ->
                directionsPairsAndCorrespondingEdges.keys
                    .map { addPairs(it, coordinate) } //Get all the neighbours
                    .filter { neighbour -> salmonCoordinates.contains(neighbour) }//filter out non-salmons
                /**At this point the nodes are of type salmons and edges are between two direct neighbours only if
                both of them are salmon
                Thus we still need to filter out every node that has more than two neighbours
                Note that we already know at this point that each node would have at least one salmon neighbour,
                so no need for checking the lower bound */

            }.filterValues {  neighbours ->  neighbours.size <=2  }
            graph

        }
        val salmonGraph = makeSalmonGraph(player.habitat)
        val visited : MutableSet<Pair<Int, Int>> = mutableSetOf()
        val isB = rootSerivce.currentGame!!.ruleSet[Animal.SALMON.ordinal]
        val scoreMap = if (isB) mapOf(1 to 2, 2 to 4, 3 to 9, 4 to 11, 5 to 17)
        else mapOf(1 to 2, 2 to 5, 3 to 8, 4 to 12, 5 to 16, 7 to 25)
        val maxRuns = if (isB) 7 else 5
        var salmonRuns = 0
        for(salmonNode in salmonGraph.keys.filter { !visited.contains(it) }){
            salmonRuns+= depthFirstLongestPathAt(salmonGraph, visited, salmonNode)

        }
        return scoreMap.getOrDefault(maxOf(salmonRuns, maxRuns) ,0)
    }

    /**
     *
     */
    private fun calculateFoxScore(player : Player) {
        //ToDo
    }

}