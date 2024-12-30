package service

import entity.Animal
import entity.HabitatTile
import entity.Player
import entity.Terrain


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootService: RootService) : AbstractRefreshingService() {


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
        private val getNeighbours: (Pair<Int, Int>) -> List<Pair<Int, Int>> = { pair ->
            directionsPairsAndCorrespondingEdges.keys.map { addPairs(pair, it) }
        }

        private fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
            return directionsPairsAndCorrespondingEdges.keys.map { addPairs(it, this) }
        }

        /**
         * A class aggregating all the detailed bonus points
         * @property animalsScores a Map for Animal, Score pairs for each animal
         * @property ownLongestTerrainsScores a map for the longest connected [Terrain]s for each one
         * @property natureTokens the number of nature tokens left for the player
         * @property longestAmongOtherPlayers the bonus points the player becomes when having the longest Terrains
         */
        data class PlayerScore(
            val animalsScores: Map<Animal, Int>,
            val ownLongestTerrainsScores: Map<Terrain, Int>,
            val natureTokens: Int = 0,
            var longestAmongOtherPlayers: Map<Terrain, Int> =
                Terrain.values().associateWith { 0 }.toMutableMap()
        ) {
            val sum: () -> Int = {
                animalsScores.values.sum() + ownLongestTerrainsScores.values.sum() +
                        longestAmongOtherPlayers.values.sum() + natureTokens
            }
        }

        /**
         * Calculating the longest path starting at some coordinates
         * @param coordinate the start coordinate
         * @param graph the graph to search
         * @param visited the visited coordinates so far
         */
        private fun depthFirstConnectedComponentLength(
            graph: Map<Pair<Int, Int>, List<Pair<Int, Int>>>,
            visited: MutableSet<Pair<Int, Int>>,
            coordinate: Pair<Int, Int>
        ): Int {
            if (visited.contains(coordinate) || !graph.containsKey(coordinate)) return 0
            var connectedComponentLength: Int = 1
            visited.add(coordinate)
            val neighbours = graph[coordinate] ?: listOf()


            for (neighbour in neighbours) {
                if (!visited.contains(neighbour))
                    connectedComponentLength += depthFirstConnectedComponentLength(graph, visited, neighbour)
            }
            return connectedComponentLength

        }
    }

    /**
     *
     */
    fun calculateScore(player: Player): Int {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
        return 0
    }

    /***
     * Calculating the longest connected terrains of some type for some player
     * @param type the wished [Terrain] type
     * @param player The [Player] having this longest terrains
     * @return [Int] representing the longest connected combination of [Terrain]s at this [Player.habitat]
     */
    fun calculateLongestTerrain(searchedTerrain: Terrain, player: Player): Int {
        data class TileAndCoordinate(val tile: HabitatTile, val coordinate: Pair<Int, Int>) {

            val hasSearchedTerrain: Boolean = tile.terrains.any { it == searchedTerrain }

            /**Checking if this tileAndCoordinate is connected with  the other TileAndCoordinate
             * and that the corresponding edges in both tileAndCoordinates refers to searchedTerrainEdges
             * returns true iff this TileAndCoordinate has at least one Edge of type searchedTerrain,
             * and the other TileAndCoordinate has at least one Edge of type searchedTerrain
             * and both of them are connecting at one of those edges.
             */
            val youAndMeHaveConnectingSearchedTerrainEdge: (another: TileAndCoordinate) -> Boolean =
                { anotherTileAndCoordinate ->
                    val hisRelativePlace = Pair(
                        anotherTileAndCoordinate.coordinate.first - this.coordinate.first,
                        anotherTileAndCoordinate.coordinate.second - this.coordinate.second
                    )
                    val hisEdge = directionsPairsAndCorrespondingEdges[hisRelativePlace]
                    checkNotNull(hisEdge)

                    val result = anotherTileAndCoordinate.tile.terrains[hisEdge] == searchedTerrain &&
                            this.tile.terrains[(hisEdge + 3).mod(6)] == searchedTerrain

                    result
                }
        }

        val buildSearchedTerrainGraph: (Map<Pair<Int, Int>, TileAndCoordinate>) -> Map<Pair<Int, Int>, List<Pair<Int, Int>>> =
            { playerTilesAndCoordinate ->
                val tilesAndCoordinatesWithSearchedTerrain =
                    playerTilesAndCoordinate.filterValues { it.hasSearchedTerrain }
                val graph =
                    tilesAndCoordinatesWithSearchedTerrain.mapValues { (coordinate, parentTileAndCoordinate) ->
                        val neighboursWithSearchedTerrain =
                            coordinate.neighbours().mapNotNull { tilesAndCoordinatesWithSearchedTerrain[it] }
                        val neighboursWithSearchedTerrainConnectedAtRightEdge =
                            neighboursWithSearchedTerrain.filter { neighbourTileAndCoordinate ->
                                parentTileAndCoordinate.youAndMeHaveConnectingSearchedTerrainEdge(
                                    neighbourTileAndCoordinate
                                )
                            }
                        neighboursWithSearchedTerrainConnectedAtRightEdge.map { it.coordinate }
                    }
                graph
            }
        val searchedTerrainGraph =
            buildSearchedTerrainGraph(player.habitat.mapValues { TileAndCoordinate(it.value, it.key) })
        val visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
        var longestConnectedComponent = 0
        for (terrainNode in searchedTerrainGraph.keys) {
            if (!visited.contains(terrainNode))
                longestConnectedComponent = maxOf(
                    longestConnectedComponent,
                    depthFirstConnectedComponentLength(searchedTerrainGraph, visited, terrainNode)
                )
        }
        return longestConnectedComponent
    }


    /**
     *Calculates the Score resulted from the bear collection
     * @param player the player whose score should be calculated
     * @return [Int] representing the score resulted from the Bear combinations of this player based on
     * the current [entity.CascadiaGame.ruleSet]
     */
    private fun calculateBearScore(player: Player): Int {
        val makeBearGraph: (Map<Pair<Int, Int>, HabitatTile>) -> Map<Pair<Int, Int>, List<Pair<Int, Int>>> =
            { habitatTiles ->
                val bearNodesCoordinates = habitatTiles.filterValues { it.wildlifeToken?.animal == Animal.BEAR }
                    .keys.toSet()

                val graph = bearNodesCoordinates.associateWith { coordinate ->
                    coordinate
                        .neighbours()
                        .filter { neighbour -> bearNodesCoordinates.contains(neighbour) }

                }
                graph
            }
        val bearGraph = makeBearGraph(player.habitat)
        val visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val game = checkNotNull(rootService.currentGame) { "No Game started yet!" }
        val isB = game.ruleSet[Animal.BEAR.ordinal]
        val searchedLength = if (isB) 3 else 2
        var connectedComponentsWithSearchedLength = 0

        for (bearNode in bearGraph.keys) {
            if (!visited.contains(bearNode)) {
                if (depthFirstConnectedComponentLength(bearGraph, visited, bearNode) == searchedLength) {
                    connectedComponentsWithSearchedLength++
                }
            }
        }
        if (isB)
            return 10 * connectedComponentsWithSearchedLength
        else
            return when (connectedComponentsWithSearchedLength) {
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
    private fun calculateElkScore(player: Player): Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateHawkScore(player: Player): Int {
        //ToDo
        return 0
    }

    /**
     * Calculating the scores for the salmon runs
     * @param player the [Player] to calculate its runs.
     * @return an [Int] of salmon score for the given [Player] based on the current [entity.CascadiaGame.ruleSet]
     */
    private fun calculateSalmonScore(player: Player): Int {
        val hasSalmonToken: (HabitatTile) -> Boolean = { it.wildlifeToken?.animal == Animal.SALMON }
        val makeSalmonGraph: (Map<Pair<Int, Int>, HabitatTile>) -> Map<Pair<Int, Int>, List<Pair<Int, Int>>> =
            { habitatTile ->
                val salmonCoordinates = habitatTile.filterValues { hasSalmonToken(it) }.keys.toSet()
                val graph = salmonCoordinates.associateWith { coordinate ->
                    coordinate
                        .neighbours()
                        .filter { neighbour -> salmonCoordinates.contains(neighbour) }//filter out non-salmons
                    /**At this point the nodes are of type salmons and edges are between two direct neighbours only if
                    both of them are salmon
                    Thus we still need to filter out every node that has more than two neighbours
                    Note that we already know at this point that each node would have at least one salmon neighbour,
                    so no need for checking the lower bound */
                }.filterValues { neighbours -> neighbours.size <= 2 }
                graph
            }
        val salmonGraph = makeSalmonGraph(player.habitat)
        val visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val isB = checkNotNull(rootService.currentGame) { "No game started yet" }.ruleSet[Animal.SALMON.ordinal]
        val scoreMap = if (isB) mapOf(1 to 2, 2 to 4, 3 to 9, 4 to 11, 5 to 17)
        else mapOf(1 to 2, 2 to 5, 3 to 8, 4 to 12, 5 to 16, 7 to 25)
        val maxRuns = if (isB) 7 else 5
        var salmonRuns = 0
        for (salmonCoordinate in salmonGraph.keys) {
            if (!visited.contains(salmonCoordinate))
                salmonRuns += depthFirstConnectedComponentLength(salmonGraph, visited, salmonCoordinate)
        }
        return scoreMap.getOrDefault(maxOf(salmonRuns, maxRuns), 0)
    }


    /**
     *Adds the Points from the foxes to the players score according to the current rule for foxes
     *
     * @param player the player for witch the score shoud be calculated
     */
    private fun calculateFoxScore(player: Player): Int {
        val foxes = mutableListOf<Pair<Int, Int>>()
        val habitat = player.habitat
        var points = 0

        //gets all foxes
        habitat.forEach {
            if (it.value.wildlifeToken?.animal == Animal.FOX) {
                foxes.add(it.key)
            }
        }

        foxes.forEach {
            val animals = intArrayOf(0, 0, 0, 0, 0, 0)
            val game = rootService.currentGame
            checkNotNull(game)

            val neighbours = it.neighbours()

            //counts the animals
            neighbours.forEach { neighbour ->
                {
                    animals[habitat[neighbour]?.wildlifeToken?.animal?.ordinal ?: 5]++
                }
            }

            //resets the fallback value for animals that are null
            animals[5] = 0

            if (game.ruleSet[Animal.FOX.ordinal]) {
                //B
                var pairs = 0
                animals[Animal.FOX.ordinal] = 0
                animals.forEach { animal ->
                    {
                        if (animal >= 2) {
                            pairs++
                        }
                    }
                }

                if (pairs == 1) points += 3
                if (pairs == 2) points += 5
                if (pairs == 3) points += 7
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
                points += differentAnimals
            }
        }
        return points
    }
}