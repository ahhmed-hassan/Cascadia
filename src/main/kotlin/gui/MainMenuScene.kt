package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class MainMenuScene : MenuScene(1920, 1080) {

    val startHotSeatGameButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 400,
        text = "Start HotSeat Game",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    val hostNetworkGameButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 470,
        text = "Host Network Game",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    val joinNetworkGameButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 540,
        text = "Join Network Game",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    val exitButton = Button(
        width = 100, height = 50,
        posX = 920, posY = 740,
        text = "Exit",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        addComponents(
            startHotSeatGameButton,
            hostNetworkGameButton,
            joinNetworkGameButton,
            exitButton
        )

    }
}