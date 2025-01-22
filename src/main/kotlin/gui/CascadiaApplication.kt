package gui

import service.RootService
import service.ScoringService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Main class of the Cascadia application
 */
class CascadiaApplication : BoardGameApplication("Cascadia Game"), Refreshables {

    private val rootService = RootService()

    private val hotSeatConfigurationMenu = HotSeatConfigurationMenuScene(rootService)

    private val networkConfigurationMenuScene = NetworkConfigurationMenuScene(rootService)

    private val networkJoinMenuScene = NetworkJoinMenuScene(rootService)

    private val gameScene = GameScene(
        rootService,
        hotSeatConfigurationMenu,
        networkJoinMenuScene,
        networkConfigurationMenuScene
    )

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

    private val winningMenuScene = WinningMenuScene(rootService,gameScene).apply {
        exitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        rootService.addRefreshables(
            this,
            gameScene,
            hotSeatConfigurationMenu,
            networkJoinMenuScene,
            networkConfigurationMenuScene,
            winningMenuScene,
        )
        this.showMenuScene(mainMenuScene, 0)
    }

    override fun refreshAfterGameEnd(scores: Map<String, ScoringService.Companion.PlayerScore>) {
        gameScene.enqueueRefresh {
            this.showMenuScene(winningMenuScene)
        }
    }

    override fun refreshAfterGameStart() {
        hideMenuScene()
        this.showGameScene(gameScene)
    }

}