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
            if (visited.contains(coordinate)) return 0
            var connectedComponentLength: Int = 1
            visited.add(coordinate)
            val neighbours = graph[coordinate] ?: listOf()

            for (neighbour in neighbours) {
                if (!visited.contains(neighbour))
                    connectedComponentLength += depthFirstConnectedComponentLength(graph, visited, neighbour)
            }
            return connectedComponentLength

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
     * Calculates all the scores for each player, including individual animal scores, longest terrain scores,
     * nature tokens, and bonus scores for having the longest terrains among all players.
     *
     * @return A map where the keys are player names and the values are `PlayerScore` objects containing the
     * detailed breakdown of their scores.
     *
     * @throws IllegalStateException if no game has been started.
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

    /***
     * Calculating the longest connected terrains of some type for some player
     * @param searchedTerrain the wished [Terrain] type
     * @param player The [Player] having this longest terrains
     * @return [Int] representing the longest connected combination of [Terrain]s at this [Player.habitat]
     */
    private fun calculateLongestTerrain(searchedTerrain: Terrain, player: Player): Int {

        val hasAtLeastOneEdgeOfSearchedTerrain: (HabitatTile) -> Boolean = { it.terrains.any { it == searchedTerrain } }
        val buildSearchedTerrainGraph: (Map<Pair<Int, Int>, HabitatTile>) -> Map<Pair<Int, Int>, List<Pair<Int, Int>>> =
            { playerTiles ->
                val searchedTerrainNodesCoordinates =
                    playerTiles.filterValues { hasAtLeastOneEdgeOfSearchedTerrain(it) }
                        .keys.toSet()

                val graph = searchedTerrainNodesCoordinates.associateWith { coordinate ->
                    coordinate
                        .neighbours()
                        .filter { neighbour -> searchedTerrainNodesCoordinates.contains(neighbour) }
                }
                graph

            }
        val searchedTerrainGraph = buildSearchedTerrainGraph(player.habitat)
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
     * Adds the Points from the elk to the players score according to the current rule for elks
     *
     * @param player the person you want to add the score to
     */
    private fun calculateElkScore(player : Player) {
        //filters out all the elks on the map
        val elkCoordinate = player.habitat.filterValues { it.wildlifeToken?.animal == Animal.ELK }.keys.toMutableSet()
        //gets the ruleset
        val isB = checkNotNull(rootService.currentGame).ruleSet[Animal.ELK.ordinal]

        //ruleset A
        if(!isB) {
            for (i in 3 downTo 0) {
                //checks for every Elk if it is in a row with i other Elks
                for (coordinate in elkCoordinate) {
                    val straightLine = (coordinate.second + 0..coordinate.second + i).all { y ->
                        elkCoordinate.contains(Pair(coordinate.first, y))
                    }
                    //when a straight line has been found it checks which length it has and removes it from the
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
     * Adds the score for the hawks to the player according to the current rule for hawks
     *
     * @param player the person you want to add the score to
     */
    private fun calculateHawkScore(player : Player) {
        //filters out all the hawks on the map
        val hawkCoordinate = player.habitat.filterValues { it.wildlifeToken?.animal == Animal.HAWK}.keys.toMutableSet()
        //gets the ruleset
        val isB = checkNotNull(rootService.currentGame).ruleSet[Animal.HAWK.ordinal]

        //implementing one Set of pairs for rule a
        val notAdjacent: MutableSet<Pair<Int, Int>> = mutableSetOf()

        for(coordinate in hawkCoordinate){
            //checks for every hawk if it is not adjacent to any other hawks
            val neighbours = getNeighbours(coordinate)
            if (neighbours.none { it in hawkCoordinate }) {
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
            //implementing one set of pairs for rule b
            val inSight: MutableSet<Pair<Int, Int>> = mutableSetOf()
            //checks if a hawk is also in direct sight to another hawk
            for(coordinate in notAdjacent){
                for(innerCoordinate in hawkCoordinate){
                    //vertical
                    if(coordinate.second == innerCoordinate.second){
                        inSight.add(coordinate)
                    }
                    //horizontal
                    if(coordinate.first == innerCoordinate.first){
                        inSight.add(coordinate)
                    }
                    //diagonal plus
                    if(coordinate.first - innerCoordinate.first == coordinate.second - innerCoordinate.second){
                        inSight.add(coordinate)
                    }
                    //diagonal minus
                    if(coordinate.first - innerCoordinate.first == -(coordinate.second - innerCoordinate.second)){
                        inSight.add(coordinate)
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
     * @param player the player for witch the score should be calculated
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