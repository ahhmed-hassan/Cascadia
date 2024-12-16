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
     * Adds the score for the hawks to the player according to the current rule for hawks
     *
     * @param player the person you want to add the score to
     */
    private fun calculateHawkScore(player : Player) {
        //filters out all the hawks on the map
        val hawkCoordinate = player.habitat.filterValues { it.wildlifeToken?.animal == Animal.HAWK}.keys.toMutableSet()
        //gets the ruleset
        val isB = checkNotNull(rootSerivce.currentGame).ruleSet[Animal.HAWK.ordinal]

        //implementing one Set of Pairs for rule a and one for rule b
        val notAdjacent: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val inSight : MutableSet<Pair<Int, Int>> = mutableSetOf()

        for(coordinate in hawkCoordinate){
            //checks for every hawk if it is not adjacent to any other hawks
            if( Pair(coordinate.first -1, coordinate.second +1) !in hawkCoordinate &&
                Pair(coordinate.first, coordinate.second +1) !in hawkCoordinate &&
                Pair(coordinate.first +1, coordinate.second) !in hawkCoordinate &&
                Pair(coordinate.first +1, coordinate.second -1) !in hawkCoordinate &&
                Pair(coordinate.first , coordinate.second -1) !in hawkCoordinate &&
                Pair(coordinate.first -1, coordinate.second) !in hawkCoordinate){
                    notAdjacent.add(coordinate)
                }
        }

        if(!isB) {
            //scores for ruleset a
            if(notAdjacent.size==1) {player.score += 2}
            if(notAdjacent.size==2) {player.score += 5}
            if(notAdjacent.size==3) {player.score += 8}
            if(notAdjacent.size==4) {player.score += 11}
            if(notAdjacent.size==5) {player.score += 14}
            if(notAdjacent.size==6) {player.score += 18}
            if(notAdjacent.size==7) {player.score += 22}
            if(notAdjacent.size>=8) {player.score += 26}
        } else {
            //checks if a hawk is also in direct sight to another hawk
            for(coordinate in notAdjacent){
                for(innerCoordinate in notAdjacent){
                    //vertical
                    if(coordinate.second == innerCoordinate.second){
                        inSight.add(coordinate)
                        inSight.add(innerCoordinate)
                    }
                    //horizontal
                    if(coordinate.first == innerCoordinate.first){
                        inSight.add(coordinate)
                        inSight.add(innerCoordinate)
                    }
                    //diagonal plus
                    if(coordinate.first - innerCoordinate.first == coordinate.second - innerCoordinate.second){
                        inSight.add(coordinate)
                        inSight.add(innerCoordinate)
                    }
                    //diagonal minus
                    if(coordinate.first - innerCoordinate.first == -(coordinate.second - innerCoordinate.second)){
                        inSight.add(coordinate)
                        inSight.add(innerCoordinate)
                    }
                }
            }
            //scores for ruleset b
            if(inSight.size==2) {player.score += 5}
            if(inSight.size==3) {player.score += 9}
            if(inSight.size==4) {player.score += 12}
            if(inSight.size==5) {player.score += 16}
            if(inSight.size==6) {player.score += 20}
            if(inSight.size==7) {player.score += 24}
            if(inSight.size==8) {player.score += 28}
        }
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