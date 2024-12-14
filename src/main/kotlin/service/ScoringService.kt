package service

import entity.Animal
import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootSerivce: RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun calculateScore(player: Player) {
        //ToDo

        onAllRefreshables { /*ToDo*/ }
    }

    /**
     *
     */
    private fun calculateLongestTerrain(type: Terrain, player: Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateBearScore(player: Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateElkScore(player: Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateHawkScore(player: Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateSalmonScore(player: Player) {
        //ToDo
    }

    /**
     *
     */
    private fun calculateFoxScore(player: Player) {
        val foxes = mutableListOf<Pair<Int, Int>>()
        val habitat = player.habitat
        //gets all foxes
        habitat.forEach {
            if (it.value.wildlifeToken?.animal == Animal.FOX)
                foxes.add(it.key)
        }

        foxes.forEach {
            
        }
    }

}