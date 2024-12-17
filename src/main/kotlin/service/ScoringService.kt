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