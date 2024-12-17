package gui

import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * The Main Menu Scene of the Game to choose the Mode
 *
 * @param rootService The root service to which this scene belongs
 */

class MainMenuScene (val rootService: RootService): MenuScene(1920, 1080) {

    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 900,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 900 / 2,
        visual = ColorVisual(Color(0xA6C9A3))
    )

    val startHotSeatGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 100,
        text = "Start HotSeat Game",
        alignment = Alignment.CENTER,
        font = Font(32),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
    }

    val hostNetworkGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 300,
        text = "Host Network Game",
        alignment = Alignment.CENTER,
        font = Font(32),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
    }

    val joinNetworkGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 500,
        text = "Join Network Game",
        alignment = Alignment.CENTER,
        font = Font(32),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
    }

    val exitButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 700,
        text = "Exit",
        alignment = Alignment.CENTER,
        font = Font(32),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        contentPane.addAll(
            startHotSeatGameButton,
            hostNetworkGameButton,
            joinNetworkGameButton,
            exitButton
        )
        addComponents(contentPane)
    }
}