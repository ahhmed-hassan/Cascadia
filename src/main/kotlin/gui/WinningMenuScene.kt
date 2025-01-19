package gui

import entity.Animal
import entity.Terrain
import service.RootService
import service.ScoringService
import tools.aqua.bgw.components.layoutviews.GridPane
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

    private val gridPane = GridPane<UIComponent>(
        posX = 960,
        posY = 500,
        columns = 6,
        rows = 14,
        spacing = 10
    ).apply {
        visual = ColorVisual(Color(0xA6C9A3))
    }

    val exitButton = Button(
        width = 200,
        height = 50,
        posX = 860,
        posY = 980,
        text = "Exit",
        visual = ColorVisual(255, 255, 255)
    ).apply {
        onMouseClicked = {}
    }

    private val playerImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("person.png")
    )

    private val bearImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("bear.png")
    )

    private val elkImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("elk.png")
    )

    private val hawkImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("hawk.png")
    )

    private val salmonImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("salmon.png")
    )

    private val foxImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("fox.png")
    )

    private val forestImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("forest.png")
    )

    private val mountainImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("mountain.png")
    )

    private val prairieImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("prairies.png")
    )

    private val wetlandImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("wetlands.png")
    )

    private val riverImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("river.png")
    )

    private val tokenImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("naturetoken.png")
    )

    private val totalImage = Label(
        width = 60,
        height = 60,
        posX = 200,
        posY = 150,
        visual = ImageVisual("crown.png")
    )

    init {
        background = ImageVisual("Cascadia.jpg")
        addComponents(gridPane, exitButton)
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

        gridPane[0, 0] = playerImage
        gridPane[0, 1] = bearImage
        gridPane[0, 2] = elkImage
        gridPane[0, 3] = hawkImage
        gridPane[0, 4] = salmonImage
        gridPane[0, 5] = foxImage
        gridPane[0, 6] = forestImage
        gridPane[0, 7] = mountainImage
        gridPane[0, 8] = prairieImage
        gridPane[0, 9] = wetlandImage
        gridPane[0, 10] = riverImage
        gridPane[0, 11] = tokenImage
        gridPane[0, 12] = totalImage


        sortedScores.forEachIndexed { index, (playerName, score) ->
            val totalScore = score.animalsScores.values.sum() + score.ownLongestTerrainsScores.values.sum() + score.natureTokens
            gridPane[index + 1, 0] = createScoreLabel(playerName)
            gridPane[index + 1, 1] = createScoreLabel(score.animalsScores[Animal.BEAR].toString())
            gridPane[index + 1, 2] = createScoreLabel(score.animalsScores[Animal.ELK].toString())
            gridPane[index + 1, 3] = createScoreLabel(score.animalsScores[Animal.HAWK].toString())
            gridPane[index + 1, 4] = createScoreLabel(score.animalsScores[Animal.SALMON].toString())
            gridPane[index + 1, 5] = createScoreLabel(score.animalsScores[Animal.FOX].toString())
            gridPane[index + 1, 6] = createScoreLabel(score.ownLongestTerrainsScores[Terrain.FOREST].toString())
            gridPane[index + 1, 7] = createScoreLabel(score.ownLongestTerrainsScores[Terrain.MOUNTAIN].toString())
            gridPane[index + 1, 8] = createScoreLabel(score.ownLongestTerrainsScores[Terrain.PRAIRIE].toString())
            gridPane[index + 1, 9] = createScoreLabel(score.ownLongestTerrainsScores[Terrain.WETLAND].toString())
            gridPane[index + 1, 10] = createScoreLabel(score.ownLongestTerrainsScores[Terrain.RIVER].toString())
            gridPane[index + 1, 11] = createScoreLabel(score.natureTokens.toString())
            gridPane[index + 1, 12] = createScoreLabel(totalScore.toString())
        }
    }

    private fun createScoreLabel(text: String): Label {
        return Label(
            width = 150,
            height = 50,
            alignment = Alignment.CENTER,
            text = text,
            font = Font(size = 20)
        )
    }
}
