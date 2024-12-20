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

        //nature tokens are added to the score
        player.score += player.natureToken

        onAllRefreshables { /*ToDo*/ }
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