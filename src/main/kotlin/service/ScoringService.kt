package service

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
            if(visited.contains(coordinate)) return 0
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
    fun calculateScore(player : Player): Int {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
        return 0
    }

    /**
     *
     */
    private fun calculateLongestTerrain(type : Terrain, player : Player):Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateBearScore(player : Player): Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateElkScore(player : Player): Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateHawkScore(player : Player) : Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateSalmonScore(player : Player): Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateFoxScore(player : Player): Int {
        //ToDo
        return 0
    }

}