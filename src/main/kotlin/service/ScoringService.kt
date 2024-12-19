package service

import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootService : RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun calculateScore(player : Player): Int {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
        return 0
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