package gui

import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class HotSeatConfigurationMenuScene (val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val playerNameFields = mutableListOf<TextField>()
    private val playerButtons = mutableListOf<List<Button>>()

    private val addPlayerButton = Button(
        width = 50, height = 50,
        posX = 750, posY = 530,
        text = "+",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            if (playerNameFields.size < 4) {
                val newField = createPlayerNameField(150 + playerNameFields.size * 100)
                playerNameFields.add(newField)
                addComponents(newField)
                val buttons = createPlayerButtons(playerNameFields.size - 1)
                playerButtons.add(buttons)
                buttons.forEach { addComponents(it) }
            }
        }
    }

    private fun createPlayerNameField(posY: Int): TextField {
        return TextField(
            width = 200, height = 50,
            posX = 600, posY = posY,
            text = "Enter Name",
            visual = ColorVisual(255, 255, 255)
        )
    }

    private fun createPlayerButtons(index: Int): List<Button> {
        val humanButton = Button(
            width = 50, height = 50,
            posX = 850, posY = 150 + index * 100,
            text = "H",
            visual = ImageVisual("human.png")
        ).apply {
            onMouseClicked = {
            }
        }

        val easyButton = Button(
            width = 50, height = 50,
            posX = 920, posY = 150 + index * 100,
            text = "E",
            visual = ImageVisual("easy-robot.png")
        ).apply {
            onMouseClicked = {
            }
        }

        val normalButton = Button(
            width = 50, height = 50,
            posX = 990, posY = 150 + index * 100,
            text = "NL",
            visual = ImageVisual("normal-robot.png")
        ).apply {
            onMouseClicked = {
            }
        }

        return listOf(humanButton, easyButton, normalButton)
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
            rootService.gameService.startNewGame(mapOf("a" to PlayerType.LOCAL), listOf(true))
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")

        // Initialize two players by default
        for (i in 0 until 2) {
            val playerNameField = createPlayerNameField(150 + i * 100)
            playerNameFields.add(playerNameField)
            addComponents(playerNameField)
            val buttons = createPlayerButtons(i)
            playerButtons.add(buttons)
            buttons.forEach { addComponents(it) }
        }

        addComponents(
            addPlayerButton,
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