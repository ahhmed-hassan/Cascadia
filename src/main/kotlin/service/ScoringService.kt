package service

import entity.Animal
import entity.Terrain
import entity.Player


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
        ){
            val sum : () -> Int = {animalsScores.values.sum() + ownLongestTerrainsScores.values.sum() +
                    longestAmongOtherPlayers.values.sum() + natureTokens}
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
     * TODO
     */
    fun calculateScoresForThreeORMorePlayers(): Map<Player, PlayerScore> {
        //TODO
        val game = checkNotNull(rootService.currentGame) { "No game started yet" }
        return emptyMap()
    }

    /**
     *Calculates the detailed scores for every player in the game
     * preconditions : The game has only two players
     * returns a map from each player to the [PlayerScore] object having all the infos for the scoring boards
     */
    fun calculateScoresForTwoPlayers(): Map<Player, PlayerScore> {
        val game = checkNotNull(rootService.currentGame) { "No game started yet" }
        check(game.playerList.size==2) {"Only call this when having only two players"}

        val playersScores = game.playerList.associateWith { player ->
            PlayerScore(animalsScores = mapOf(
                Animal.BEAR to calculateBearScore(player),
                Animal.SALMON to calculateSalmonScore(player),
                Animal.ELK to calculateElkScore(player),
                Animal.FOX to calculateFoxScore(player),
                Animal.HAWK to calculateHawkScore(player)
            )
                , ownLongestTerrainsScores = Terrain.values().associateWith { calculateLongestTerrain(it, player) }
                , natureTokens = player.natureToken)
        }

        val (firstPlayer, secondPlayer) = playersScores.keys.toList()
        val firstPlayerLongestTerrains = playersScores[firstPlayer]?.ownLongestTerrainsScores ?: emptyMap()
        val secondPlayerLongestTerrains = playersScores[secondPlayer]?.ownLongestTerrainsScores ?: emptyMap()

        val firstPlayerBonusMap = Terrain.values().associateWith {
            if (firstPlayerLongestTerrains[it]!! > secondPlayerLongestTerrains[it]!!) 2
            else 0
        }
        val secondPlayerBonusMap = Terrain.values().associateWith {
            if (secondPlayerLongestTerrains[it]!! > firstPlayerLongestTerrains[it]!!) 2
            else 0
        }

        return playersScores.toMutableMap().apply {
            this[firstPlayer] = this[firstPlayer]!!.copy(longestAmongOtherPlayers = firstPlayerBonusMap)
            this[secondPlayer] = this[secondPlayer]!!.copy(longestAmongOtherPlayers = secondPlayerBonusMap)
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
     */
    private fun calculateFoxScore(player: Player): Int {
        //ToDo
        return 0
    }

}