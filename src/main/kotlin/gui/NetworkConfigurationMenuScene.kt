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
    private val createId = TextField(
        width = 200, height = 50,
        posX = 600, posY = 300,
        text = "Create Game ID",
        visual = ColorVisual(255, 255, 255)
    )

    private val humanButton = Button(
        width = 50, height = 50,
        posX = 850, posY = 200,
        text = "H",
        visual = ImageVisual("human.png")
    ).apply {
        onMouseClicked = {
        }
    }

    private val easyButton = Button(
        width = 50, height = 50,
        posX = 920, posY = 200,
        text = "E",
        visual = ImageVisual("easy-robot.png")
    ).apply {
        onMouseClicked = {
        }
    }

    private val normalButton = Button(
        width = 50, height = 50,
        posX = 990, posY = 200,
        text = "NL",
        visual = ImageVisual("normal-robot.png")
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


    private val bearImage = Button(
        width = 50, height = 50,
        posX = 1500, posY = 300,
        visual = ImageVisual("bear.png")
    )

    private val bearToggleButton = Button(
        width = 50, height = 50,
        posX = 1550, posY = 300,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val elkImage = Button(
        width = 50, height = 50,
        posX = 1500, posY = 350,
        visual = ImageVisual("elk.png")
    )

    private val elkToggleButton = Button(
        width = 50, height = 50,
        posX = 1550, posY = 350,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val hawkImage = Button(
        width = 50, height = 50,
        posX = 1500, posY = 400,
        visual = ImageVisual("hawk.png")
    )

    private val hawkToggleButton = Button(
        width = 50, height = 50,
        posX = 1550, posY = 400,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val salmonImage = Button(
        width = 50, height = 50,
        posX = 1500, posY = 450,
        visual = ImageVisual("salmon.png")
    )

    private val salmonToggleButton = Button(
        width = 50, height = 50,
        posX = 1550, posY = 450,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val foxImage = Button(
        width = 50, height = 50,
        posX = 1500, posY = 500,
        visual = ImageVisual("fox.png")
    )

    private val foxToggleButton = Button(
        width = 50, height = 50,
        posX = 1550, posY = 500,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
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
            createId,
            humanButton,
            easyButton,
            normalButton,
            randomOrderButton,
            randomRuleButton,
            startButton,
            bearImage, bearToggleButton,
            elkImage, elkToggleButton,
            hawkImage, hawkToggleButton,
            salmonImage, salmonToggleButton,
            foxImage, foxToggleButton
        )
    }


}
