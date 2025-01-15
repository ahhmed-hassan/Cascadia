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

class NetworkConfigurationMenuScene (val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val playerNameFields = mutableListOf<TextField>()
    private val playerButtons = mutableListOf<Button>()
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

    private val simEntry = ComboBox(
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
        posX = 1050,
        posY = 400,
        text = "Create Game ID",
        visual = ColorVisual(255, 255, 255)
    )

    private fun createPlayerButtons(): Button {
        val playerTypeButton = Button(
            width = 50,
            height = 50,
            posX = 450,
            posY = 300,
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

    private val randomRuleToggle = ToggleButton(
        width = 250,
        height = 50,
        posX = 400,
        posY = 800,
        text = "Random Rule",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    )

    private val randomOrderToggle = ToggleButton(
        width = 250,
        height = 50,
        posX = 100,
        posY = 800,
        text = "Random Order",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    )

    private val startButton = Button(
        width = 250,
        height = 50,
        posX = 1100,
        posY = 800,
        text = "Start",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            if (randomOrderToggle.isSelected) { randomOrder = true }
            if (randomRuleToggle.isSelected) { randomRule = true}
            rules = determineRules()
            rootService.networkService.startNewHostedGame(orderIsRanom = randomOrder, isRandomRules = randomRule, scoreRules = rules)
            rules.clear()

        }
    }

    private val createHostGameButton = Button(
        width = 250,
        height = 50,
        posX = 800,
        posY = 800,
        text = "Host Game",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            val playerNames = playerNameFields.filter { it.text.isNotBlank() }.map { it.text }
            val playerTypes = playerButtons.filter { it.text.isNotBlank() }.map { it.text }
            val param = mapPlayerToPlayerTypes(playerNames,playerTypes)
            rootService.networkService.hostGame(secret = "cascadia24d", name = playersField.text, sessionID = createId.text, playerType=param.values.first())
        }
    }

    private val networkStatusArea = TextArea(
        width = 300,
        height = 35,
        posX = 1050,
        posY = 500,
    ).apply {
        isDisabled = true
        // only visible when the text is changed to something non-empty
        isVisible = false
        textProperty.addListener { _, new ->
            isVisible = new.isNotEmpty()
        }
    }

    private val cancelButton = Button(
        width = 140,
        height = 35,
        posX = 1050,
        posY = 600,
        text = "Cancel"
    ).apply {
        visual = ColorVisual(221, 136, 136)
        isVisible = false
        onMouseClicked = {
            rootService.networkService.disconnect()
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
            randomRuleToggle,
            randomOrderToggle,
            startButton,
            createHostGameButton,
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
            networkStatusArea,
        )
        addComponents(overlay)
        val buttons = createPlayerButtons()
        playerNameFields.add(playersField)
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


    override fun refreshConnectionState(newState: ConnectionState) {
        networkStatusArea.text = newState.toUIText()
        val disconnected = newState == ConnectionState.DISCONNECTED
        cancelButton.isVisible = !disconnected
        startButton.isVisible = disconnected
        createHostGameButton.isVisible = disconnected
    }

    /**
     * Refreshes the player list when a new player joins.
     *
     * @param networkPlayers A list of player names.
     */
    override fun refreshAfterPlayerJoined(networkPlayers: MutableList<String>) {
        playerNameFields.forEach { overlay.remove(it) }
        playerNameFields.clear()

        networkPlayers.forEachIndexed { index, playerName ->
            val playerNameField = TextField(
                width = 200,
                height = 50,
                posX = 200,
                posY = 300 + index * 100,
                text = playerName,
                visual = ColorVisual(255, 255, 255)
            )
            playerNameFields.add(playersField)
            overlay.add(playerNameField)
        }
        startButton.isVisible = networkPlayers.size >=1
    }

}
