package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class NetworkConfigurationMenuScene : MenuScene(1920, 1080) {

    private val playersField = TextField(
        width = 200, height = 50,
        posX = 600, posY = 200,
        text = "Enter Name",
        visual = ColorVisual(255, 255, 255)
    )

    private val waitingPlayersField = TextField(
        width = 200, height = 50,
        posX = 600, posY = 300,
        text = "Waiting Players",
        visual = ColorVisual(255, 255, 255)
    )

    private val networkButton = Button(
        width = 50, height = 50,
        posX = 850, posY = 300,
        text = "NW",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val humanButton = Button(
        width = 50, height = 50,
        posX = 850, posY = 200,
        text = "H",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val easyButton = Button(
        width = 50, height = 50,
        posX = 920, posY = 200,
        text = "E",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val normalButton = Button(
        width = 50, height = 50,
        posX = 990, posY = 200,
        text = "NL",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val ownOrderButton = Button(
        width = 150, height = 50,
        posX = 600, posY = 600,
        text = "OwnOrder",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val randomOrderButton = Button(
        width = 150, height = 50,
        posX = 800, posY = 600,
        text = "RandomOrder",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val chosenRuleButton = Button(
        width = 150, height = 50,
        posX = 1300, posY = 600,
        text = "Chosen Rule",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val randomRuleButton = Button(
        width = 150, height = 50,
        posX = 1500, posY = 600,
        text = "Random Rule",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    val startButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 800,
        text = "Start",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
        }
    }


    init {
        background = ImageVisual("Cascadia.jpg")
        addComponents(
            playersField,
            waitingPlayersField,
            networkButton,
            humanButton,
            easyButton,
            normalButton,
            ownOrderButton,
            randomOrderButton,
            chosenRuleButton,
            randomRuleButton,
            startButton)
    }


}
