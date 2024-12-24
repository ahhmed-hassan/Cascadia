package service

import entity.Animal
import entity.Player
import entity.Terrain


/**
 *  Service class for scoring of all players at the end of a [entity.CascadiaGame]
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
     * Calculates bonus points for three or more players based on their longest terrain scores.
     *
     * @param playersLongestTerrain A map containing each player and their terrain-specific scores.
     * @return A map where each player is mapped to their bonus points for each terrain.
     */
    private fun calculateBonusForThreeOrMorePlayers(playersLongestTerrain: Map<String, Map<Terrain, Int>>)
            : Map<String, Map<Terrain, Int>> {
        val makeTerrainPlayerBonusMap: () -> Map<Terrain, Map<String, Int>> = {
            Terrain.values().associateWith { terrain ->
                val terrainsScores = playersLongestTerrain.mapValues {
                    val terrainValue = checkNotNull(it.value[terrain])
                    terrainValue
                }

                val sortedPlayerScores: List<Pair<String, Int>> = terrainsScores.toList()
                    .sortedByDescending { terrainsScores[it.first] }

                val bonuses = mutableMapOf<String, Int>()
                val largestScore = sortedPlayerScores.first().second
                val secondLargestScore =
                    sortedPlayerScores.dropWhile { it.second == largestScore }.firstOrNull()?.second

                val playersWithLargestScore = sortedPlayerScores.filter { it.second == largestScore }.map { it.first }
                val largestScoreBonusBasedOnNumberOfPlayersAchieved = mapOf(1 to 3, 2 to 2)
                playersWithLargestScore.forEach { player ->
                    bonuses[player] = largestScoreBonusBasedOnNumberOfPlayersAchieved.getOrDefault(
                        playersWithLargestScore.size, 1
                    )
                }

                if (secondLargestScore != null) {
                    val playersWithSecondLargestScore: List<String> = sortedPlayerScores
                        .filter { it.second == secondLargestScore }
                        .map { it.first }
                    playersWithSecondLargestScore.forEach { player ->
                        bonuses[player] = if (playersWithSecondLargestScore.size == 1) 1 else 0
                    }
                }
                bonuses
            }
        }
        val terrainBonusScore = makeTerrainPlayerBonusMap()
        val playerTerrainBonusMap: Map<String, Map<Terrain, Int>> =
            terrainBonusScore.entries.flatMap { (terrain, playerMap) ->
                playerMap.entries.map { (player, bonus) -> player to (terrain to bonus) }
            }
                .groupBy(/*keySelector*/{ (player, _) -> player },
                    /**Value transformer
                     * Without this we would have a map from the player to a  List<Pair<Player, Pair<Terrain, Int>>>
                     * which what we basically after flatMap call have
                     */
                    { it.second })
                /**
                 * Here we are transforming the list of pairs to map
                 */
                .mapValues { (_, terrainBonusList) -> terrainBonusList.toMap() }

        return playerTerrainBonusMap
    }
    /**
     * TODO
     */

    /**
     * Calculates the detailed scores for every player in the game
     * returns a map from each player to the [PlayerScore] object having all the infos for the scoring boards
     */
    fun calculateBonusScores(playersLongestTerrainMap: Map<String, Map<Terrain, Int>>): Map<String, Map<Terrain, Int>> {
        val game = checkNotNull(rootService.currentGame) { "No game started yet" }

        if (game.playerList.size > 2) {
            return calculateBonusForThreeOrMorePlayers(playersLongestTerrainMap)
        }

        val (firstPlayer, secondPlayer) = playersLongestTerrainMap.keys.toList()


        val firstPlayerLongestTerrains = checkNotNull(playersLongestTerrainMap[firstPlayer])
        val secondPlayerLongestTerrains = checkNotNull(playersLongestTerrainMap[secondPlayer])

        val firstPlayerBonusMap = Terrain.values().associateWith {
            if (checkNotNull(firstPlayerLongestTerrains[it]) > checkNotNull(secondPlayerLongestTerrains[it])) 2
            else if (checkNotNull(firstPlayerLongestTerrains[it]) == checkNotNull(secondPlayerLongestTerrains[it])) 1
            else 0
        }
        val secondPlayerBonusMap = Terrain.values().associateWith {
            if (checkNotNull(secondPlayerLongestTerrains[it]) > checkNotNull(firstPlayerLongestTerrains[it])) 2
            else if (checkNotNull(secondPlayerLongestTerrains[it]) == checkNotNull(firstPlayerLongestTerrains[it])) 1
            else 0
        }

        return mapOf(firstPlayer to firstPlayerBonusMap, secondPlayer to secondPlayerBonusMap)
    }

    /**
     *
     */
    fun calculateScore(): Map<String, PlayerScore> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet" }

        val playersScores = game.playerList.associate { player ->
            player.name to
                    PlayerScore(
                        animalsScores = mapOf(
                            Animal.BEAR to calculateBearScore(player),
                            Animal.SALMON to calculateSalmonScore(player),
                            Animal.ELK to calculateElkScore(player),
                            Animal.FOX to calculateFoxScore(player),
                            Animal.HAWK to calculateHawkScore(player)
                        ),
                        ownLongestTerrainsScores = Terrain.values()
                            .associateWith { calculateLongestTerrain(it, player) },
                        natureTokens = player.natureToken
                    )
        }

        val terrainScoresByPlayer: Map<String, Map<Terrain, Int>> = playersScores.mapValues { (_, playerScore) ->
            playerScore.ownLongestTerrainsScores
        }

        val bonus = calculateBonusScores(terrainScoresByPlayer)

        return playersScores.mapValues { (playerName: String, playerScore: PlayerScore) ->
            val playerBonus = checkNotNull(bonus[playerName])
            playerScore.copy(
                longestAmongOtherPlayers = playerBonus
            )
        }
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