package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

class CascadiaApplication : BoardGameApplication("Cascadia Game") {

    private val rootService = RootService()

    private val gameScene = GameScene(rootService)

    private val hotSeatConfigurationMenu = HotSeatConfigurationMenuScene(rootService).apply {
        startButton.onMouseClicked = {
            hideMenuScene()
            showGameScene(gameScene)
        }
    }

    private val networkConfigurationMenuScene = NetworkConfigurationMenuScene(rootService).apply {
        startButton.onMouseClicked = {
            hideMenuScene()
            showGameScene(gameScene)
        }
    }

    private val networkJoinMenuScene = NetworkJoinMenuScene(rootService).apply {
        joinButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(winningMenuScene)
        }
    }

    private val mainMenuScene = MainMenuScene(rootService).apply {
        startHotSeatGameButton.onMouseClicked = {
            showMenuScene(hotSeatConfigurationMenu)
        }
        hostNetworkGameButton.onMouseClicked = {
            showMenuScene(networkConfigurationMenuScene)
        }
        joinNetworkGameButton.onMouseClicked = {
            showMenuScene(networkJoinMenuScene)
        }
        exitButton.onMouseClicked = {
            exit()
        }
    }

    private val winningMenuScene = WinningMenuScene(rootService).apply {
        exitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        this.showMenuScene(mainMenuScene, 0)
    }

    private fun showGameScene() {
        this.showGameScene(gameScene)
    }

    private fun showWinningMenuScene() {
        this.showMenuScene(winningMenuScene)
    }
}