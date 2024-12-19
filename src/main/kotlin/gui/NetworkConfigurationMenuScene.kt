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

class NetworkConfigurationMenuScene (val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val playerNameFields = mutableListOf<TextField>()
    private val playerButtons = mutableListOf<Button>()
    private var rules = mutableListOf<Boolean>()
    private var randomRule = false


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
        text = "Hosting Network Game",
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

    private val playersField = TextField(
        width = 200,
        height = 50,
        posX = 200,
        posY = 300,
        text = "Enter Name",
        visual = ColorVisual(255, 255, 255)
    )
    private val createId = TextField(
        width = 200,
        height = 50,
        posX = 200,
        posY = 360,
        text = "Create Game ID",
        visual = ColorVisual(255, 255, 255)
    )

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

    val startButton = Button(
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
            randomRuleButton,
            startButton,
            playersField,
            createId,
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
        val buttons = createPlayerButtons(300)
        playerButtons.add(buttons)
        overlay.add(buttons)
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
    ) {
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


    }


}
