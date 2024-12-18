package service

import entity.Animal
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
     *
     */
    private fun calculateSalmonScore(player : Player) {
        //ToDo
    }

    /**
     *Adds the Points from the foxes to the players score according to the current rule for foxes
     *
     * @param player the player for witch the score shoud be calculated
     */
    private fun calculateFoxScore(player: Player) {
        val foxes = mutableListOf<Pair<Int, Int>>()
        val habitat = player.habitat
        //gets all foxes
        habitat.forEach {
            if (it.value.wildlifeToken?.animal == Animal.FOX) {
                foxes.add(it.key)
            }
        }

        foxes.forEach {
            val animals = intArrayOf(0, 0, 0, 0, 0, 0)
            val game = rootSerivce.currentGame
            checkNotNull(game)

            //counts the animals
            animals[habitat[Pair(it.first + 1, it.second - 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first, it.second - 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first - 1, it.second)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first - 1, it.second + 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first, it.second + 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first + 1, it.second)]?.wildlifeToken?.animal?.ordinal ?: 5]++

            //resets the fallback value for animals that are null
            animals[5] = 0

            if (game.ruleSet[Animal.FOX.ordinal]) {
                //B
                var pairs = 0
                animals.forEach { animal ->
                    {
                        if (animal >= 2) {
                            pairs++
                        }
                    }
                }

                if (pairs == 1) player.score += 3
                if (pairs == 2) player.score += 5
                if (pairs == 3) player.score += 7
            } else {
                //A
                var differentAnimals = 0
                animals.forEach { animal ->
                    {
                        if (animal >= 1) {
                            differentAnimals++
                        }
                    }
                }
                player.score += differentAnimals
            }
        }
    }
}