package gui

import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color
import kotlin.random.Random

class HotSeatConfigurationMenuScene (val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val playerNameFields = mutableListOf<TextField>()
    private val playerButtons = mutableListOf<Button>()
    private val downButtons = mutableListOf<Button>()
    private val upButtons = mutableListOf<Button>()
    private var rules = mutableListOf<Boolean>()
    private var randomRule = false
    private var randomOrder = false

    private val overlay = Pane<UIComponent>(
        posX = 200,
        posY = 80,
        width = 1480,
        height = 920,
        visual = ColorVisual(Color(0xA6C9A3))
    )

    private val titleHotSeat = Label(
        posX = 0,
        posY = 0,
        width = 1480,
        height = 200,
        text = "HotSeatMode",
        font = Font(48)
    )

    private val titleNames = Label(
        posX = 0,
        posY = 100,
        width = 400,
        height = 300,
        text = "Configure Players",
        font = Font(32)
    )

    private val titleRule = Label(
        posX = 650,
        posY = 100,
        width = 400,
        height = 300,
        text = "Configure Rule",
        font = Font(32)
    )

    private val simSpeed = Label(
        posX = 1000,
        posY = 100,
        width = 400,
        height = 300,
        text = "Enter Simulation Speed",
        font = Font(32)
    )

    private val simEntry = TextField(
        posX = 1050,
        posY = 300,
        width = 200,
        height = 50,
        font = Font(24)
    )

    private val addPlayerButton = Button(
        width = 50,
        height = 50,
        posX = 200,
        posY = 480,
        text = "+",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            if (playerNameFields.size < 4) {
                val newField = createPlayerNameField(300 + playerNameFields.size * 100)
                playerNameFields.add(newField)
                overlay.add(newField)
                val buttons = createPlayerButtons(300 + (playerNameFields.size - 1) * 100)
                playerButtons.add(buttons)
                overlay.add(buttons)
                val downButton = createDownSymbol(300 + (playerNameFields.size - 1) * 100)
                downButtons.add(downButton)
                downButton.apply {
                    onMouseClicked ={
                        changeDown(downButton)
                    }
                }
                overlay.add(downButton)
                val upButton = createUpSymbol(300 + (playerNameFields.size - 1) * 100)
                upButtons.add(upButton)
                upButton.apply {
                    onMouseClicked = {
                        changeUp(upButton)
                    }
                }
                overlay.add(upButton)
                posY += 100
                if (playerNameFields.size == 4){
                    isDisabled = true
                    isVisible = false
                }
            }
        }
    }

    private fun createPlayerNameField(posY: Int): TextField {
        return TextField(
            width = 200,
            height = 50,
            posX = 200,
            posY = posY,
            text = "",
            font = Font(32),
            visual = ColorVisual(255, 255, 255)
        )
    }

    private fun createPlayerButtons(posY: Int): Button {
        val playerTypeButton = Button(
            width = 50,
            height = 50,
            posX = 450,
            posY = posY,
            text = "H",
            visual = ImageVisual("human.png")
        ).apply {
            onMouseClicked = {
                when (text){
                    "H" -> {
                        this.visual = ImageVisual("easy-robot.png")
                        text = "E"
                    }
                    "E"-> {
                        this.visual = ImageVisual("normal-robot.png")
                        text = "NL"
                    }
                    "NL" -> {
                        this.visual = ImageVisual("human.png")
                        text = "H"
                    }
                    else -> println("Something wrong")
                }
            }
        }

        return playerTypeButton
    }

    private fun createDownSymbol(posY: Int) : Button{
        return Button(
            width = 50,
            height = 50,
            posY = posY,
            posX = 50,
            visual = ImageVisual("downward.png")
        )
    }

    private fun createUpSymbol(posY: Int) : Button{
        return Button(
            width = 50,
            height = 50,
            posY = posY,
            posX = 100,
            visual = ImageVisual("upward.png")
        )
    }


    private val randomOrderButton = Button(
        width = 250,
        height = 50,
        posX = 100,
        posY = 800,
        text = "RandomOrder",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            randomOrder = true
            visual = ColorVisual(Color.GRAY)
        }
    }

    private val bearImage = Label(
        width = 60,
        height = 60,
        posX = 800,
        posY = 300,
        visual = ImageVisual("bear.png")
    )

    private val bearToggleButton = Button(
        width = 60,
        height = 60,
        posX = 860,
        posY = 300,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val elkImage = Label(
        width = 60,
        height = 60,
        posX = 800,
        posY = 360,
        visual = ImageVisual("elk.png")
    )

    private val elkToggleButton = Button(
        width = 60,
        height = 60,
        posX = 860,
        posY = 360,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val hawkImage = Label(
        width = 60,
        height = 60,
        posX = 800,
        posY = 420,
        visual = ImageVisual("hawk.png")
    )

    private val hawkToggleButton = Button(
        width = 60,
        height = 60,
        posX = 860,
        posY = 420,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val salmonImage = Label(
        width = 60,
        height = 60,
        posX = 800,
        posY = 480,
        visual = ImageVisual("salmon.png")
    )

    private val salmonToggleButton = Button(
        width = 60,
        height = 60,
        posX = 860,
        posY = 480,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val foxImage = Label(
        width = 60,
        height = 60,
        posX = 800,
        posY = 540,
        visual = ImageVisual("fox.png")
    )

    private val foxToggleButton = Button(
        width = 60,
        height = 60,
        posX = 860,
        posY = 540,
        text = "A",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            text = if (text == "A") "B" else "A"
        }
    }

    private val randomRuleButton = Button(
        width = 250,
        height = 50,
        posX = 400,
        posY = 800,
        text = "Random Rule",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            randomRule = true
            visual = ColorVisual(Color.GRAY)
        }
    }

    private val startButton = Button(
        width = 250,
        height = 50,
        posX = 1000,
        posY = 800,
        text = "Start",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            val playerNames = playerNameFields.filter { it.text.isNotBlank() }.map { it.text }
            val playerTypes = playerButtons.filter { it.text.isNotBlank() }.map { it.text }
            val param = mapPlayerToPlayerTypes(playerNames,playerTypes)
            val rules = determineRules()
            //rootService.gameService.startNewGame(playerNames = param, scoreRules = rules)
            println(param)
            rules.clear()
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")

        overlay.addAll(
            titleNames,
            titleHotSeat,
            titleRule,
            simSpeed,
            simEntry,
            addPlayerButton,
            randomOrderButton,
            randomRuleButton,
            startButton,
            bearImage,
            bearToggleButton,
            elkImage,
            elkToggleButton,
            hawkImage,
            hawkToggleButton,
            salmonImage,
            salmonToggleButton,
            foxImage,
            foxToggleButton,
        )
        addComponents(overlay)

        // Initialize two players by default
        for (i in 0 until 2) {
            val playerNameField = createPlayerNameField(300 + i * 100)
            playerNameFields.add(playerNameField)
            overlay.add(playerNameField)
            val buttons = createPlayerButtons(300 + i * 100)
            playerButtons.add(buttons)
            overlay.add(buttons)
            val downButton = createDownSymbol(300 + i * 100)
            downButtons.add(downButton)
            downButton.apply {
                onMouseClicked ={
                    changeDown(downButton)
                }
            }
            overlay.add(downButton)
            val upButton = createUpSymbol(300 + i * 100)
            upButtons.add(upButton)
            upButton.apply {
                onMouseClicked = {
                    changeUp(upButton)
                }
            }
            overlay.add(upButton)
        }
    }

    /**
     * [determineRules] looks at each Animal Button and looks whether the Button shows A or B.
     * If Button shows A then it equals false
     * If Button shows B then it equals true
     * If Rules are to be determined Random based on if button RandomRule is pressed, this method
     * puts a random order of false and true in a list
     */
    private fun determineRules(): MutableList<Boolean> {

        if (!randomRule) {
            if (bearToggleButton.text == "A") {
                rules.add(false)
            } else
                rules.add(true)

            if (elkToggleButton.text == "A") {
                rules.add(false)
            } else
                rules.add(true)

            if (foxToggleButton.text == "A") {
                rules.add(false)
            } else
                rules.add(true)

            if (hawkToggleButton.text == "A") {
                rules.add(false)
            } else
                rules.add(true)

            if (salmonToggleButton.text == "A") {
                rules.add(false)
            } else
                rules.add(true)
        }
        else{
            for (i in 0..4)
                rules.add(Random.nextBoolean())
        }

        return rules
    }

    /**
     * [mapPlayerToPlayerTypes] takes as Input the list of names and list of types based on the Symbol,
     * and maps these
     *
     * @return Map<Sting,PlayerTypes> needed for StartNewGame
     */
    private fun mapPlayerToPlayerTypes(
        names: List<String>,
        types: List<String>,
    ): Map<String, PlayerType> {
        val pairs: MutableMap<String, PlayerType> = mutableMapOf()

        for (i in names.indices) {
            val playerType = when (types[i]) {
                "H" -> PlayerType.LOCAL
                "E" -> PlayerType.EASY
                "NL" -> PlayerType.NORMAL
                else -> throw IllegalArgumentException("Unknown type: ${types[i]}")
            }
            pairs[names[i]] = playerType
        }

        return if (randomOrder) {
            // Shuffle the entries and reconstruct the map
            pairs.entries.shuffled().associate { it.toPair() }
        } else {
            pairs
        }
    }

    private fun changeDown(down : Button){
        val index = downButtons.indexOf(down)
        when (index){
            0 -> {
                val swap = playerNameFields[0].text
                playerNameFields[0].text = playerNameFields[1].text
                playerNameFields[1].text = swap
            }
            1-> {
                val swap = playerNameFields[1].text
                playerNameFields[1].text = playerNameFields[2].text
                playerNameFields[2].text = swap
            }
            2-> {
                val swap = playerNameFields[2].text
                playerNameFields[2].text = playerNameFields[3].text
                playerNameFields[3].text = swap
            }
            else -> {}
        }
    }

    private fun changeUp (up : Button){
        val index = upButtons.indexOf(up)
        when (index){
            1 -> {
                val swap = playerNameFields[0].text
                playerNameFields[0].text = playerNameFields[1].text
                playerNameFields[1].text = swap
                val swap2 = playerButtons[0].text
                playerButtons[0].text = playerButtons[1].text
                playerButtons[1].text = swap2
            }
            2-> {
                val swap = playerNameFields[1].text
                playerNameFields[1].text = playerNameFields[2].text
                playerNameFields[2].text = swap
            }
            3-> {
                val swap = playerNameFields[2].text
                playerNameFields[2].text = playerNameFields[3].text
                playerNameFields[3].text = swap
            }
            else -> {}
        }
    }
}