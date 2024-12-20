package service

import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootService : RootService) : AbstractRefreshingService() {

    /** [calculateScore] is responsible for calculating the player scores.
     *
     * @param player The player whose score will get calculated.
     *
     */
    fun calculateScore(player : Player) {
        val game = rootService.currentGame
        checkNotNull(game)

        //calculates the largest habitat of each terrain
        for(terrain in Terrain.values()){
            calculateLongestTerrain(terrain,player)
        }

        //calculates all the wildlife scoring card patterns
        calculateBearScore(player)
        calculateElkScore(player)
        calculateHawkScore(player)
        calculateSalmonScore(player)
        calculateFoxScore(player)

        when(game.playerList.size){
            2 -> for(type in Terrain.values()){
                    val list = longestTerrainBetweenPlayers(type)
                    if(list[0].second == list[1].second){
                        list[0].first.score += 1
                        list[1].first.score += 1
                    }
                    else{
                        list[0].first.score += 2
                    }
            }

            3 -> for(type in Terrain.values()){
                    val list = longestTerrainBetweenPlayers(type)
                    if(list[0].second > list[1].second){
                        list[0].first.score += 3
                        if(list[1].second > list[2].second){
                            list[1].first.score += 1
                        }
                    }
                    else if(list[0].second == list[1].second && list[1].second == list[2].second){
                        list[0].first.score += 1
                        list[1].first.score += 1
                        list[2].first.score += 1
                    }
                    else if(list[0].second == list[1].second && list[1].second > list[2].second){
                        list[0].first.score += 2
                        list[1].first.score += 2
                    }
            }

            4 -> for(type in Terrain.values()){
                val list = longestTerrainBetweenPlayers(type)
                if(list[0].second > list[1].second){
                    list[0].first.score += 3
                    if(list[1].second > list[2].second){
                        list[1].first.score += 1
                    }
                }
                else if(list[0].second == list[1].second && list[1].second == list[2].second &&
                    list[2].second == list[3].second){

                    list[0].first.score += 1
                    list[1].first.score += 1
                    list[2].first.score += 1
                    list[3].first.score += 1
                }
                else if(list[0].second == list[1].second && list[1].second == list[2].second &&
                    list[2].second > list[3].second){

                    list[0].first.score += 1
                    list[1].first.score += 1
                    list[2].first.score += 1
                }
                else if(list[0].second == list[1].second && list[1].second > list[2].second){
                    list[0].first.score += 2
                    list[1].first.score += 2
                }
            }
        }

        //nature tokens are added to the score
        player.score += player.natureToken

        onAllRefreshables { /*ToDo*/ }
    }

    private fun longestTerrainBetweenPlayers(type: Terrain): List<Pair<Player, Int>> {
        val game = rootService.currentGame
        checkNotNull(game)

        val playerTerrains = game.playerList
            .map { player -> Pair(player, calculateLongestTerrain(type, player)) } // Spieler und Terrain-Länge als Pair

        return playerTerrains.sortedByDescending { it.second }
    }

    /**
     *
     */
    private fun calculateLongestTerrain(type : Terrain, player : Player): Int {
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