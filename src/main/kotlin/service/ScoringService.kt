package service

import entity.Animal
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

        /**
         * creates the pattern needed for ruleset B
         * @param coordinate coordinate of the highest tile in the pattern
         * @param number the amount of tiles you want to have in the pattern
         *
         * @return List with the coordinates of tiles in the pattern
         */
        private fun createPattern(coordinate: Pair<Int,Int>,number: Int) : List<Pair<Int,Int>> {
            if(number==3) {
                return listOf(
                    Pair(coordinate.first, coordinate.second),
                    Pair(coordinate.first - 1, coordinate.second + 1),
                    Pair(coordinate.first - 1, coordinate.second),
                    Pair(coordinate.first - 2, coordinate.second + 1))
            }
            if(number==2) {
                return listOf(
                    Pair(coordinate.first, coordinate.second),
                    Pair(coordinate.first - 1, coordinate.second + 1),
                    Pair(coordinate.first - 1, coordinate.second))
            }
            if(number==1) {
                return listOf(
                    Pair(coordinate.first, coordinate.second),
                    Pair(coordinate.first, coordinate.second - 1),
                )
            }
            else {
                return listOf(Pair(coordinate.first, coordinate.second))
            }
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
     * Adds the Points from the elk to the players score according to the current rule for elks
     *
     * @param player the person you want to add the score to
     */
    private fun calculateElkScore(player : Player) {
        //filters out all the elks on the map
        val elkCoordinate = player.habitat.filterValues { it.wildlifeToken?.animal == Animal.ELK }.keys.toMutableSet()
        //gets the ruleset
        val isB = checkNotNull(rootSerivce.currentGame).ruleSet[Animal.ELK.ordinal]

        //ruleset A
        if(!isB) {
            for (i in 3 downTo 0) {
                //checks for every Elk if it is in a row with i other Elks
                for (coordinate in elkCoordinate) {
                    val straightLine = (coordinate.second + 0..coordinate.second + i).all { y ->
                        elkCoordinate.contains(Pair(coordinate.first, y))
                    }
                    //when a straight line has been fund it checks which length it has and removes it from the
                    //elkCoordinate pair
                    if (straightLine) {
                        if (i == 3) {
                            player.score += 13
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 1))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 2))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 3))
                        } else if (i == 2) {
                            player.score += 9
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 1))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 2))
                        } else if (i == 1) {
                            player.score += 5
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second))
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second + 1))
                        } else {
                            player.score += 2
                            elkCoordinate.remove(Pair(coordinate.first, coordinate.second))
                        }
                    }
                }
            }
        } else {
            for(i in 3 downTo 0) {
                for (coordinate in elkCoordinate) {
                    //creates the pattern that fits the amount of tiles
                    val pattern = createPattern(coordinate,i)
                    //checks if it is an elk
                    val isMatch = pattern.all { it in elkCoordinate }
                    //checks which score must be given and what needs to be removed
                    if(isMatch && i==3) {
                        player.score += 13
                        elkCoordinate.removeAll(pattern)
                    }
                    if(isMatch && i==2) {
                        player.score += 9
                        elkCoordinate.removeAll(pattern)
                    }
                    if(isMatch && i==1) {
                        player.score += 5
                        elkCoordinate.removeAll(pattern)
                    }
                    if(isMatch && i==0) {
                        player.score += 2
                        elkCoordinate.removeAll(pattern)
                    }
                }
            }
        }
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