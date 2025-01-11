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

class NetworkJoinMenuScene (val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val playerNameFields = mutableListOf<TextField>()
    private val playerButtons = mutableListOf<Button>()

    private val overlay = Pane<UIComponent>(
        posX = 200,
        posY = 80,
        width = 1480,
        height = 920,
        visual = ColorVisual(Color(0xA6C9A3))
    )


    private val titleLabel = Label(
        posX = 0,
        posY = 0,
        width = 1480,
        height = 200,
        text = "Join Network Game",
        font = Font(48)
    )


    private val playersField = TextField(
        width = 200,
        height = 50,
        posX = 600,
        posY = 300,
        text = "Enter Name",
        visual = ColorVisual(255, 255, 255)
    )
    private val gameId = TextField(
        width = 200,
        height = 50,
        posX = 600,
        posY = 360,
        text = "Enter Game ID",
        visual = ColorVisual(255, 255, 255)
    )

    private fun createPlayerButtons(posY: Int): Button {
        val playerTypeButton = Button(
            width = 50,
            height = 50,
            posX = 850,
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

    val startButton = Button(
        width = 250,
        height = 50,
        posX = 600,
        posY = 800,
        text = "Start",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            val playerNames = playerNameFields.filter { it.text.isNotBlank() }.map { it.text }
            val playerTypes = playerButtons.filter { it.text.isNotBlank() }.map { it.text }
            val param = mapPlayerToPlayerTypes(playerNames,playerTypes)
            val secret = "Server secret"
            val name = playersField.text
            val sessionID = gameId.text

            rootService.networkService.joinGame(secret, name, sessionID, PlayerType.NETWORK)
            //rootService.gameService.startNewGame(playerNames = param, scoreRules = rules)
            println(param)
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        overlay.addAll(
            titleLabel,
            startButton,
            playersField,
            gameId,
        )
        addComponents(overlay)
        val buttons = createPlayerButtons(300)
        playerButtons.add(buttons)
        overlay.add(buttons)
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
}
