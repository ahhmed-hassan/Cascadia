package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class WinningMenuScene : MenuScene(1920, 1080) {

    private val titleLabel = Label(
        width = 400, height = 50,
        posX = 760, posY = 100,
        text = "Score",
    )

    private val scoreLabels = listOf(
        Label(
            width = 400, height = 50,
            posX = 760, posY = 200,
            text = "1. Player1 : 42"
        ),
        Label(
            width = 400, height = 50,
            posX = 760, posY = 260,
            text = "2. Player2 : 32"
        ),
        Label(
            width = 400, height = 50,
            posX = 760, posY = 320,
            text = "3. Player3 : 22"
        ),
        Label(
            width = 400, height = 50,
            posX = 760, posY = 380,
            text = "4. Player4 : 20"
        )
    )

    val exitButton = Button(
        width = 100, height = 50,
        posX = 900, posY = 500,
        text = "Exit",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        addComponents(titleLabel, exitButton)
        scoreLabels.forEach { addComponents(it) }
    }
}
