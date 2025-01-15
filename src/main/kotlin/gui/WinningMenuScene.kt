package gui

import service.RootService
import service.ScoringService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

class WinningMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshables {

    private val overlay = Pane<UIComponent>(
        posX = 610,
        posY = 90,
        width = 700,
        height = 900,
        visual = ColorVisual(Color(0xA6C9A3))
    )

    private val titleLabel = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 50,
        text = "Score",
        alignment = Alignment.CENTER,
        font = Font(32),
    )

    private val crownImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("crown.png")
    )

    private val player1Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 150,
        alignment = Alignment.CENTER,
        font = Font(20)
    )

    private val player2Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 210,
        alignment = Alignment.CENTER,
        font = Font(20)
    )

    private val player3Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 270,
        alignment = Alignment.CENTER,
        font = Font(20)
    )

    private val player4Label = Label(
        width = 400,
        height = 50,
        posX = 150,
        posY = 330,
        alignment = Alignment.CENTER,
        font = Font(20)
    )

    val exitButton = Button(
        width = 100,
        height = 50,
        posX = 300,
        posY = 450,
        text = "Exit",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {
            // Exit the game or return to the main menu
        }
    }

    init {
        background = ImageVisual("Cascadia.jpg")
        overlay.addAll(
            titleLabel,
            crownImage,
            player1Label,
            player2Label,
            player3Label,
            player4Label,
            exitButton
        )
        addComponents(overlay)
        updateScores()
    }

    override fun refreshAfterGameEnd(scores: Map<String, ScoringService.Companion.PlayerScore>) {
        updateScores()
    }

    private fun updateScores() {
        val game = rootService.currentGame
        checkNotNull(game) { return }

        val scores = rootService.scoringService.calculateScore()
        val sortedScores = scores.entries.sortedByDescending { it.value.sum() }

        val playerLabels = listOf(player1Label, player2Label, player3Label, player4Label)

        playerLabels.forEachIndexed { index, label ->
            label.text = ""
            sortedScores.getOrNull(index)?.let { (name, score) ->
                label.text = "${index + 1}. $name : ${score.sum()} (Details: Animals: ${score.animalsScores}, Terrains: ${score.ownLongestTerrainsScores}, Nature Tokens: ${score.natureTokens})"
                println(label.text)
            }
        }
    }
}