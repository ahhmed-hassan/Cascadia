package gui

import entity.HabitatTile
import entity.WildlifeToken
import service.RootService
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

class GameScene (val rootService: RootService) : BoardGameScene(1920, 1080), Refreshables {

    private val habitats : BidirectionalMap<HabitatTile,HexagonView> = BidirectionalMap()
    private val tokens : BidirectionalMap<WildlifeToken,HexagonView> = BidirectionalMap()
    private var selectedHabitat : HexagonView? = null
    private var selectedToken : HexagonView? = null

    private val shopHabitats = GridPane<HexagonView> (
        posX = 1400,
        posY = 100,
        rows = 1,
        columns = 4,
        spacing = 50,
        visual = ColorVisual(Color.GRAY)
    )

    private val shopTokens = GridPane<HexagonView> (
        posX = 1400,
        posY = 250,
        rows = 1,
        columns = 4,
        spacing = 100,
        visual = ColorVisual(Color.GRAY)
    )

    private val playArea = HexagonGrid<HexagonView>(
        posX = 1920/2,
        posY = 1080/2,
        width = 40,
        height = 40,
        coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
    ).apply {
        dropAcceptor = {dragEvent ->
            when (dragEvent.draggedComponent){
                is HexagonView -> {
                    true
                }
                else -> false
            }
        }

        onDragDropped = {
            rootService.playerActionService.addTileToHabitat(Pair(0,1))
        }
    }

    private val testHabitat1 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.WHITE).apply {
            //style.borderRadius = BorderRadius(10)
        }
    )

    private val testHabitat2 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.BLUE)
    )

    private val testHabitat3 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.GREEN)
    )


    private val testToken = HexagonView(
        size = 50,
        visual = CompoundVisual(
            ColorVisual(Color.WHITE).apply {
                //style.borderRadius = BorderRadius(10)
            },
            TextVisual("Bear")
        )
    )

    /**
    private val pick = MouseEvent(
        button = MouseButtonType.LEFT_BUTTON,
    )
    */

    private val ruleSetOverlay = Pane<UIComponent>(
        posY = 90,
        posX = 180,
        width = 1500,
        height = 800,
        visual = ColorVisual(Color(0xD47155))
    ).apply {
        isVisible = false
    }

    private val currentPlayerLabel = Label(
        width = 200,
        height = 50,
        posX = 50,
        posY = 20,
        text = "Current Player",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    )

    private val natureTokenLabel = Label(
        width = 200,
        height = 50,
        posX = 50,
        posY = 80,
        text = "NatureToken : 3",
        font = Font(24),
        visual = ColorVisual(255, 255, 255)
    )

    private val replaceWildlifeButton = Button(
        width = 200,
        height = 50,
        posX = 50,
        posY = 200,
        text = "Replace Wildlife",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val confirmReplacementButton = Button(
        width = 200,
        height = 50,
        posX = 50,
        posY = 270,
        text = "Confirm Replacement",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val resolveOverpopButton = Button(
        width = 200,
        height = 50,
        posX = 1670,
        posY = 860,
        text = "Resolve Overpop",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val discardToken = Button(
        width = 200,
        height = 50,
        posX = 1670,
        posY = 1000,
        text = "Discard Token",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
            rootService.gameService.nextTurn()
        }
    }

    private val showRuleSetButton = Button(
        width = 200,
        height = 50,
        posX = 1670,
        posY = 930,
        text = "Show RuleSet",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
            disableAll()
            ruleSetOverlay.isDisabled = false
            ruleSetOverlay.isVisible = true
        }
    }

    private val closeRuleSet = Button(
        width = 200,
        height = 50,
        posX = 1200,
        posY = 700,
        text = "Close RuleSet",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
            enableAll()
            ruleSetOverlay.isDisabled = true
            ruleSetOverlay.isVisible = false
        }
    }

    private val bearRule = Label(
        posX = 50,
        posY = 50,
        width = 420,
        height = 270,
        visual = ImageVisual("Bear_A.png")
    )

    private val elkRule = Label(
        posX = 520,
        posY = 50,
        width = 420,
        height = 270,
        visual = ImageVisual("Elk_A.png")
    )

    private val foxRule = Label(
        posX = 990,
        posY = 50,
        width = 420,
        height = 270,
        visual = ImageVisual("Fox_A.png")
    )

    private val hawkRule = Label(
        posX = 50,
        posY = 370,
        width = 420,
        height = 270,
        visual = ImageVisual("Hawk_A.png")
    )

    private val salmonRule = Label(
        posX = 520,
        posY = 370,
        width = 420,
        height = 270,
        visual = ImageVisual("Salmon_A.png")
    )


    init {
        background = ColorVisual(240, 240, 180)

        addComponents(
            currentPlayerLabel,
            natureTokenLabel,
            replaceWildlifeButton,
            confirmReplacementButton,
            resolveOverpopButton,
            showRuleSetButton,
            discardToken,
            shopTokens,
            shopHabitats,
            playArea,
            ruleSetOverlay
        )

    }

    override fun refreshAfterGameStart() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        ruleSetOverlay.addAll(
            closeRuleSet,
            bearRule,
            elkRule,
            foxRule,
            hawkRule,
            salmonRule)

        /**
         *  Missing:
         *  Initialize the BidirectionalMaps (habitats,tokens)
         *  Initialize the Shop with the elements
         *  Initialize the playHexagon based on players StartTile
         */


        //TESTING HOW IT WOULD LOOK LIKE
        for (i in 0..3){
            shopHabitats[i,0] = testHabitat1
            testHabitat1.onMouseClicked ={
                shopHabitats[i,0]?.apply {
                    posY -= 25
                }
                selectedHabitat = shopHabitats[i,0]
            }
            shopTokens[i,0] = testToken
            testToken.onMouseClicked = {
                shopTokens[i,0]?.apply {
                    posY -= 25
                }
                selectedToken = shopTokens[i,0]
            }
        }
        //shopHabitats.isVisible = false
        //shopHabitats.isDisabled = true

        playArea[0,0] = labeledHexagon1
        playArea[0,1] = testHabitat2
        playArea[-1,1] = testHabitat3

    }

    private fun disableAll() {
        // Disable all components
        listOf(
            currentPlayerLabel,
            natureTokenLabel,
            replaceWildlifeButton,
            confirmReplacementButton,
            resolveOverpopButton,
            showRuleSetButton,
            discardToken,
            shopTokens,
            shopHabitats,
            playArea,
            ruleSetOverlay
        ).forEach { it.isDisabled = true }
    }

    private fun enableAll() {
        // Disable all components
        listOf(
            currentPlayerLabel,
            natureTokenLabel,
            replaceWildlifeButton,
            confirmReplacementButton,
            resolveOverpopButton,
            showRuleSetButton,
            discardToken,
            shopTokens,
            shopHabitats,
            playArea,
            ruleSetOverlay
        ).forEach { it.isDisabled = false }
    }

    private fun createLabeledHexagonView(size: Int = 75, color: Color, labels: List<String>): HexagonView {
        require(labels.size == 6) { "There must be exactly 6 labels for the hexagon sides." }

        // Helper: Calculate the position for the middle of each side
        fun calculateSidePosition(index: Int): Pair<Double, Double> {
            val startAngle = Math.toRadians(60.0 * index - 90.0) // Start at the top side
            val endAngle = Math.toRadians(60.0 * (index + 1) - 90.0)

            // Midpoint between the two corners of the side
            val midAngle = (startAngle + endAngle) / 2.0
            val offsetX = size * cos(midAngle)
            val offsetY = size * sin(midAngle)
            return offsetX to offsetY
        }

        // Create text visuals for each side
        val textVisuals = labels.mapIndexed { index, text ->
            val (offsetX, offsetY) = calculateSidePosition(index)
            TextVisual(
                text = text,
                font = Font(size = 14, color = Color.BLACK),
                offsetX = offsetX,
                offsetY = offsetY
            )
        }

        return HexagonView(
            size = size,
            visual = CompoundVisual(
                ColorVisual(color), // Background color of the hexagon
                *textVisuals.toTypedArray() // Spread operator to include all text visuals
            )
        )
    }


    private val labeledHexagon1 = createLabeledHexagonView(
        size = 75,
        color = Color.LIGHT_GRAY,
        labels = listOf("F", "F", "F", "E", "E", "E")
    )

}
