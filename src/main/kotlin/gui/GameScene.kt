package gui

import entity.HabitatTile
import entity.WildlifeToken
import service.RootService
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

class GameScene (val rootService: RootService) : BoardGameScene(1920, 1080), Refreshables {

    private val habitats : BidirectionalMap<HabitatTile,HexagonView> = BidirectionalMap()
    private val tokens : BidirectionalMap<WildlifeToken,TokenView> = BidirectionalMap()

    private val shopHabitats = GridPane<HexagonView> (
        posX = 1400,
        posY = 100,
        rows = 1,
        columns = 4,
        spacing = 50,
        visual = ColorVisual(Color.GRAY)
    )

    private val shopTokens = GridPane<TokenView> (
        posX = 1400,
        posY = 250,
        rows = 1,
        columns = 4,
        spacing = 150,
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
        visual = ColorVisual(Color.WHITE)
    )

    private val testHabitat2 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.BLUE)
    )

    private val testHabitat3 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.GREEN)
    )


    private val testToken = TokenView(
        width = 50,
        height = 50,
        visual = ColorVisual(Color.WHITE)
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
            }
            shopTokens[i,0] = testToken
            testToken.onMouseClicked = {
                shopTokens[i,0]?.apply {
                    posY -= 25
                }
            }
        }
        //shopHabitats.isVisible = false
        //shopHabitats.isDisabled = true

        playArea[0,0] = testHabitat1
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


}
