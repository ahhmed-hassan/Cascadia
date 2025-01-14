package service

import entity.CascadiaGame
import gui.Refreshables

/**
 * Main service layer class for the dive Game.
 *
 * Allows access to the [gameService], [ScoringService] and the [playerActionService]
 * as well as the current game state in form of the [currentGame].
 */
class RootService {

    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val scoringService = ScoringService(this)
    val networkService = NetworkService(this)
    val hardBotService = HardBotService(this)
    val easyBotService = EasyBotService(this)

    var currentGame: CascadiaGame? = null

    /**
     * Adds the provided [newRefreshable] to all services connected
     * to this root service
     */
    fun addRefreshable(newRefreshable: Refreshables) {
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
        scoringService.addRefreshable(newRefreshable)
        networkService.addRefreshable(newRefreshable)
    }

    /**
     * Adds each of the provided [newRefreshables] to all services
     * connected to this root service
     */
    fun addRefreshables(vararg newRefreshables: Refreshables) {
        newRefreshables.forEach { addRefreshable(it) }
    }

}