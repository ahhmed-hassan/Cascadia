package service

import entity.Animal
import entity.Terrain
import entity.Player


/**
 *  Service class for scoring of all players at the end of a [CascadiaGame]
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class ScoringService(private val rootSerivce : RootService) : AbstractRefreshingService() {

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
     *Adds the Points from the foxes to the players score according to the current rule for foxes
     *
     * @param player the player for witch the score shoud be calculated
     */
    private fun calculateFoxScore(player: Player) {
        val foxes = mutableListOf<Pair<Int, Int>>()
        val habitat = player.habitat
        //gets all foxes
        habitat.forEach {
            if (it.value.wildlifeToken?.animal == Animal.FOX) {
                foxes.add(it.key)
            }
        }

        foxes.forEach {
            val animals = intArrayOf(0, 0, 0, 0, 0, 0)
            val game = rootSerivce.currentGame
            checkNotNull(game)

            //counts the animals
            animals[habitat[Pair(it.first + 1, it.second - 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first, it.second - 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first - 1, it.second)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first - 1, it.second + 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first, it.second + 1)]?.wildlifeToken?.animal?.ordinal ?: 5]++
            animals[habitat[Pair(it.first + 1, it.second)]?.wildlifeToken?.animal?.ordinal ?: 5]++

            //resets the fallback value for animals that are null
            animals[5] = 0

            if (game.ruleSet[Animal.FOX.ordinal]) {
                //B
                var pairs = 0
                animals.forEach { animal ->
                    {
                        if (animal >= 2) {
                            pairs++
                        }
                    }
                }

                if (pairs == 1) player.score += 3
                if (pairs == 2) player.score += 5
                if (pairs == 3) player.score += 7
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
                player.score += differentAnimals
            }
        }
    }
}