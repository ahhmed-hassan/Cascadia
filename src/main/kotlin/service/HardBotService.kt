package service

import entity.PlayerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.LinkedBlockingQueue

class HardBotService(private val rootService: RootService) {

    fun takeTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        require(game.currentPlayer.playerType == PlayerType.NORMAL)

        Thread {
            takeAsyncTurn()
        }.start()
    }

    private fun takeAsyncTurn() = runBlocking {

        val queue = LinkedBlockingQueue<EasyBotService>()

        var timeIsUp = false

        launch {
            delay(8000)
            //Todo: start first calcualtion
            delay(1000)
            timeIsUp = true
            queue.clear()
            delay(50)
            //Todo: start second calcualtion
            delay(500)
            //TODO: TakeRealTurn()
        }

        val maxNumberOfThreads = Runtime.getRuntime().availableProcessors()
    }
}