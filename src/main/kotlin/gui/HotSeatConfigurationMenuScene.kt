package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class HotSeatConfigurationMenu : MenuScene(1920, 1080) {

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
            prompt = "Enter Name",
            visual = ColorVisual(255, 255, 255)
        )
    }

    private fun createPlayerButtons(index: Int): List<Button> {
        val humanButton = Button(
            width = 50, height = 50,
            posX = 850, posY = 150 + index * 100,
            text = "H",
            visual = ColorVisual(255, 255, 255)
        ).apply {
            onMouseClicked = {
            }
        }

        val easyButton = Button(
            width = 50, height = 50,
            posX = 920, posY = 150 + index * 100,
            text = "E",
            visual = ColorVisual(255, 255, 255)
        ).apply {
            onMouseClicked = {
            }
        }

        val normalButton = Button(
            width = 50, height = 50,
            posX = 990, posY = 150 + index * 100,
            text = "NL",
            visual = ColorVisual(255, 255, 255)
        ).apply {
            onMouseClicked = {
            }
        }

        return listOf(humanButton, easyButton, normalButton)
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
            ownOrderButton,
            randomOrderButton,
            chosenRuleButton,
            randomRuleButton,
            startButton
        )
    }
}