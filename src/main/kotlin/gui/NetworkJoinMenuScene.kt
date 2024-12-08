package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class NetworkJoinMenuScene : MenuScene(1920, 1080) {
    private val titleLabel = Label(
        width = 400,
        height = 50,
        posX = 760,
        posY = 100,
        text = "Join Network Game",
    )

    private val gameIdField = TextField(
        width = 200,
        height = 50,
        posX = 810,
        posY = 200,
        text = "Enter Game ID"
    )

    private val nameField = TextField(
        width = 200,
        height = 50,
        posX = 810,
        posY = 300,
        text = "Enter Name"
    )

    private val humanButton = Button(
        width = 50,
        height = 50,
        posX = 1050,
        posY = 300,
        text = "H",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val easyButton = Button(
        width = 50,
        height = 50,
        posX = 1120,
        posY = 300,
        text = "E",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val normalButton = Button(
        width = 50,
        height = 50,
        posX = 1190,
        posY = 300,
        text = "NL",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    val joinButton = Button(
        width = 100,
        height = 50,
        posX = 810,
        posY = 500,
        text = "Join Game",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        addComponents(
            titleLabel,
            gameIdField,
            nameField,
            humanButton,
            easyButton,
            normalButton,
            joinButton
        )

    }
}
