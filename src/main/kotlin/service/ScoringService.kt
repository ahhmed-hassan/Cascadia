package service

import entity.Animal
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
            if (visited.contains(coordinate)) return 0
            var connectedComponentLength: Int = 1
            visited.add(coordinate)
            val neighbours = coordinate.neighbours()

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

    /**
     *
     */
    private fun calculateLongestTerrain(type: Terrain, player: Player): Int {
        //ToDo
        return 0
    }

    /**
     *
     */
    private fun calculateBearScore(player: Player): Int {
        //ToDo
        return 0
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
     *
     */
    private fun calculateSalmonScore(player: Player): Int {
        //ToDo
        return 0
    }

    /**
     *
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