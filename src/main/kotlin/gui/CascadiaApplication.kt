package gui

import tools.aqua.bgw.core.BoardGameApplication

class CascadiaApplication : BoardGameApplication("Cascadia Game") {

    private val gameScene = GameScene()

    private val hotSeatConfigurationMenu = HotSeatConfigurationMenu().apply {
        startButton.onMouseClicked = {
            showGameScene(gameScene)
        }
    }

    private val networkConfigurationMenuScene = NetworkConfigurationMenuScene().apply {
        startButton.onMouseClicked = {
            showGameScene(gameScene)
        }
    }

    private val networkJoinMenuScene = NetworkJoinMenuScene().apply {
        joinButton.onMouseClicked = {
            showMenuScene(winningMenuScene)
        }
    }

    private val mainMenuScene = MainMenuScene().apply {
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

    private val winningMenuScene = WinningMenuScene().apply {
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