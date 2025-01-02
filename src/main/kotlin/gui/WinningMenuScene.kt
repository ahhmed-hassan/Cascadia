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

class WinningMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val overlay = Pane<UIComponent>(
        posX = 610,
        posY = 90,
        width = 700,
        height = 900,
        visual = ColorVisual(Color(0xA6C9A3))
    )

    private val titleLabel = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 50,
        text = "Score",
        alignment = Alignment.CENTER,
        font = Font(32),
    )

    private val crownImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("crown.png")
    )

    private val player1Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 150,
        alignment = Alignment.CENTER,
        font = Font(20),
        text = "1. Player1 : 62" //${rootService.scoringService.calculateScore()}
        )

    private val player2Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 210,
        alignment = Alignment.CENTER,
        font = Font(20),
        text = "2. Player2 : 52" //${rootService.scoringService.calculateScore()}
    )

    private val player3Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 270,
        alignment = Alignment.CENTER,
        font = Font(20),
        text = "3. Player3 : 42" //${rootService.scoringService.calculateScore()}
    )

    private val player4Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 330,
        alignment = Alignment.CENTER,
        font = Font(20),
        text = "4. Player4 : 32" //${rootService.scoringService.calculateScore()}
    )

    val exitButton = Button(
        width = 100,
        height = 50,
        posX = 300,
        posY = 450,
        text = "Exit",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        overlay.addAll(
            titleLabel,
            crownImage,
            player1Label,
            player2Label,
            player3Label,
            player4Label,
            exitButton
        )
        addComponents(overlay)
    }

}