package gui


import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
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
        visual = ColorVisual(Color(0xFFFFFF))
    ).apply {
        opacity = 0.8
    }

    val titleLabel = Label(
        width = 400,
        height = 100,
        posX = 150,
        posY = 25,
        text = "Cascadia",
        alignment = Alignment.CENTER,
        font = Font(81, Color(0x333333), "JetBrains Mono ExtraBold"),
    )

    val startHotSeatGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 200,
        text = "Start HotSeat Game",
        alignment = Alignment.CENTER,
        font = Font(32, Color(0x333333), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
        onMouseExited = {
            visual = ColorVisual(Color(0xD9D9D9))
        }
        onMouseEntered = {
            visual = ColorVisual(Color(0x8f93a1))
        }
    }

    val hostNetworkGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 350,
        text = "Host Network Game",
        alignment = Alignment.CENTER,
        font = Font(32, Color(0x333333), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
        onMouseExited = {
            visual = ColorVisual(Color(0xD9D9D9))
        }
        onMouseEntered = {
            visual = ColorVisual(Color(0x8f93a1))
        }
    }

    val joinNetworkGameButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 500,
        text = "Join Network Game",
        alignment = Alignment.CENTER,
        font = Font(32, Color(0x333333), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
        onMouseExited = {
            visual = ColorVisual(Color(0xD9D9D9))
        }
        onMouseEntered = {
            visual = ColorVisual(Color(0x8f93a1))
        }
    }

    val exitButton = Button(
        width = 400,
        height = 100,
        posX = 150,
        posY = 650,
        text = "Exit",
        alignment = Alignment.CENTER,
        font = Font(32, Color(0x333333), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0xD9D9D9))
    ).apply {
        onMouseClicked = {
        }
        onMouseExited = {
            visual = ColorVisual(Color(0xD9D9D9))
        }
        onMouseEntered = {
            visual = ColorVisual(Color(0xFF605C))
        }
    }

    init {
        background = ImageVisual("Cascadia2.png")
        contentPane.addAll(
            titleLabel,
            startHotSeatGameButton,
            hostNetworkGameButton,
            joinNetworkGameButton,
            exitButton
        )
        addComponents(contentPane)
    }
}