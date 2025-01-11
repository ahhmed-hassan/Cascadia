package gui

import entity.PlayerType
import service.ConnectionState
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.*
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

    private val simEntry = ComboBox<Float>(
        posX = 1050,
        posY = 300,
        width = 200,
        height = 50,
        items = listOf(0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f),
    ).apply {
        selectedItem = 0.5f
    }

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

    private val bearToggleButton = ToggleButton(
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

    private val elkToggleButton = ToggleButton(
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

    private val hawkToggleButton = ToggleButton(
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

    private val salmonToggleButton = ToggleButton(
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

    private val foxToggleButton = ToggleButton(
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
            rootService.gameService.startNewGame(playerNames = param, scoreRules = rules, isRandomRules = randomRule, orderIsRandom = true)
            hostGame(param, rules)
            rules.clear()
        }
    }

    private val cancelButton = Button(
        width = 140, height = 35,
        posX = 210, posY = 330,
        text = "Cancel"
    ).apply {
        visual = ColorVisual(221, 136, 136)
        isVisible = false
        onMouseClicked = {
            rootService.networkService.disconnect()
        }
    }

    private val networkStatusArea = TextArea(
        width = 300, height = 35,
        posX = 50, posY = 385,
        text = ""
    ).apply {
        isDisabled = true
        // only visible when the text is changed to something non-empty
        isVisible = false
        textProperty.addListener { _, new ->
            isVisible = new.isNotEmpty()
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
            cancelButton,
            networkStatusArea
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

        return pairs
    }

    /**
     * Hosts a new game session using the player names and rules.
     *
     * @param playerNames A map of player names and their player types.
     * @param rules A list of boolean values that defines the scoring rules for the game.
     */
    private fun hostGame(playerNames: Map<String, PlayerType>, rules: List<Boolean>) {
        val secret = "Server secret"
        val name = playersField.text
        val sessionID = createId.text

        rootService.networkService.hostGame(secret, sessionID, name, PlayerType.NETWORK)
        rootService.networkService.startNewHostedGame(orderIsRandom = false, isRandomRules = randomRule, scoreRules = rules)
    }

    override fun refreshConnectionState(state: ConnectionState) {
        networkStatusArea.text = state.toUIText()
        val disconnected = state == ConnectionState.DISCONNECTED
        cancelButton.isVisible = !disconnected
        startButton.isVisible = disconnected
    }



}
