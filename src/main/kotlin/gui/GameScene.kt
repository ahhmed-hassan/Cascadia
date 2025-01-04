package gui

import entity.HabitatTile
import entity.WildlifeToken
import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.layoutviews.CameraPane
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.event.MouseButtonType
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
    private var selectedHabitatX : Int = 0
    private var selectedHabitatY : Int = 0
    private var selectedShopToken : MutableList<Int> = mutableListOf()
    private var custom : Boolean = false
    private var currentXCamera = 0
    private var currentYCamera = 0

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
        width = 0,
        height = 0,
        coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
    )

    private val playableTile = GridPane<HexagonView>(
        posX = 150,
        posY = 600,
        columns = 1,
        rows = 1,
    )

    private val playableToken = GridPane<HexagonView>(
        posX = 150,
        posY = 800,
        columns = 1,
        rows = 1,
    )

    private val targetLayout = Pane<ComponentView> (width = 1920, height = 1080)
    private val cameraPane = CameraPane(
        width = 1920,
        height = 1080,
        target = targetLayout
    )

    private val testHabitat1 = HexagonView(
        size = 75,
        visual = ColorVisual(Color.RED)
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
            ColorVisual(Color.WHITE),
            TextVisual("Bear")
        )
    )

    private val testToken1 = HexagonView(
        size = 50,
        visual = CompoundVisual(
            ColorVisual(Color.WHITE),
            TextVisual("Salmon")
        )
    )

    private val testToken2 = HexagonView(
        size = 50,
        visual = CompoundVisual(
            ColorVisual(Color.WHITE),
            TextVisual("Hawk")
        )
    )

    private val testToken3 = HexagonView(
        size = 50,
        visual = CompoundVisual(
            ColorVisual(Color.WHITE),
            TextVisual("Eagle")
        )
    )

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
            updateButtonStates("replaceWildlife")
            for (i in 0..3){
                shopTokens[i,0]?.onMouseClicked = { mouseEvent ->
                    when (mouseEvent.button){
                        MouseButtonType.LEFT_BUTTON->{
                            shopTokens[i,0]?.apply {
                                posY -= 25
                            }
                            selectedShopToken.add(i)
                        }
                        MouseButtonType.RIGHT_BUTTON->{
                            shopTokens[i,0]?.apply {
                                posY = 0.01
                            }
                            selectedShopToken.remove(i)
                        }
                        else ->{}
                    }
                }
            }
        }
    }

    private val confirmReplacementButton = Button(
        width = 200,
        height = 50,
        posX = 50,
        posY = 270,
        text = "Confirm",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
            if (custom) {
                rootService.playerActionService.chooseCustomPair(0, 0)
            }else {
                rootService.playerActionService.replaceWildlifeTokens(selectedShopToken)
            }
            updateButtonStates("default")
        }
    }

    private val chooseCustomPair = Button(
        width = 200,
        height = 50,
        posX = 50,
        posY = 340,
        text = "CustomPair",
        font = Font(24),
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
            updateButtonStates("chooseCustomPair")

            custom = true
            for (i in 0..3) {
                shopTokens[i,0]?.onMouseClicked = { mouseEvent ->
                        when (mouseEvent.button) {
                            MouseButtonType.RIGHT_BUTTON -> {
                                shopTokens[i,0]?.apply {
                                    posY = 0.01
                                }
                                enableAllTokens()
                            }

                            MouseButtonType.LEFT_BUTTON -> {
                                shopTokens[i,0]?.apply {
                                    posY -= 25
                                }
                                selectedToken = shopTokens[i,0]
                                disableTokensExcept(checkNotNull(shopTokens[i,0]))
                            }
                            else -> {}
                        }
                }

                shopHabitats[i,0]?.onMouseClicked = { mouseEvent ->
                    when (mouseEvent.button) {
                        MouseButtonType.RIGHT_BUTTON -> {
                            shopHabitats[i,0]?.apply {
                                posY = 0.01
                            }
                            enableAllHabitats()
                        }

                        MouseButtonType.LEFT_BUTTON -> {
                            shopHabitats[i,0]?.apply {
                                posY -= 25
                            }
                            selectedHabitat = shopHabitats[i,0]
                            disableHabitatsExcept(checkNotNull(shopHabitats[i,0]))
                        }
                        else -> {}
                    }
                }
            }
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
            //check overpopulation
            rootService.playerActionService.replaceWildlifeTokens(listOf(0))
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
            rootService.playerActionService.discardToken()
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

        onKeyPressed = {
            //go left with camera
            if (it.keyCode == KeyCode.A){
                println("Pressed A")
                currentXCamera += -100
                cameraPane.reposition(currentXCamera,currentYCamera)
            }

            //go up with camera
            if (it.keyCode == KeyCode.W){
                println("Pressed W")
                currentYCamera += -100
                cameraPane.reposition(currentXCamera,currentYCamera)
            }

            //go right with camera
            if (it.keyCode == KeyCode.D){
                println("Pressed D")
                currentXCamera += 100
                cameraPane.reposition(currentXCamera,currentYCamera)
            }

            //go down with camera
            if (it.keyCode == KeyCode.S){
                println("Pressed S")
                currentYCamera += 100
                cameraPane.reposition(currentXCamera,currentYCamera)
            }

            //reset the pane to original position
            if (it.keyCode == KeyCode.R){
                println("Pressed R")
                cameraPane.reposition(0,0)
                currentXCamera = 0
                currentYCamera = 0
            }
        }

        addComponents(
            cameraPane,
            currentPlayerLabel,
            natureTokenLabel,
            replaceWildlifeButton,
            confirmReplacementButton,
            chooseCustomPair,
            resolveOverpopButton,
            showRuleSetButton,
            discardToken,
            shopTokens,
            shopHabitats,
            //playArea,
            playableTile,
            playableToken,
            ruleSetOverlay
        )

    }

    override fun refreshAfterGameStart() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        targetLayout.add(playArea)

        getRuleSets()

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


        shopTokens[0,0] = testToken
        shopTokens[1,0] = testToken1
        shopTokens[2,0] = testToken2
        shopTokens[3,0] = testToken3

        shopHabitats[0,0] = labeledHexagon1
        shopHabitats[1,0] = labeledHexagon2
        shopHabitats[2,0] = labeledHexagon3
        shopHabitats[3,0] = labeledHexagon4

        for (i in 0..3){
            shopTokens[i,0]?.apply {
                onMouseClicked = {
                    selectedToken = shopTokens[i,0]
                    selectedHabitat = shopHabitats[i,0]
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
            shopHabitats[i,0]?.apply {
                onMouseClicked = {
                    selectedToken = shopTokens[i,0]
                    selectedHabitat = shopHabitats[i,0]
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
        }

        playArea[0,0] = testHabitat1
        playArea[0,1] = testHabitat2
        playArea[-1,1] = testHabitat3

        discardToken.isDisabled = true

         //if , ob resolveOverpopulation possible or already used
         //if , ob replacement oder chooseCustom possible ist
    }

    override fun refreshAfterHabitatTileAdded() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        selectedHabitat?.removeFromParent()
        playArea[selectedHabitatX,selectedHabitatY] = checkNotNull(selectedHabitat)

        playableTile[0,0] = null
        playableTile[0,0]?.isDisabled = true

        playableToken.isDisabled = false
        discardToken.isDisabled = false
    }

    override fun refreshAfterTileRotation() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        val applicableTile = playableTile[0, 0]
        checkNotNull(applicableTile)

       // get the list of terrains from Habitat
        var sideLabels:List<String> = listOf("F", "F", "F", "E", "E", "E")

        //for rotation change the order of terrains
        for (i in 0..3){
            sideLabels = listOf( sideLabels.last()) + sideLabels.dropLast(1)
        }

        // create new Hexagon for the rotated Tile
        val newHexagon = createLabeledHexagonView(
            size = 75,
            color = Color.LIGHT_GRAY,
            labels = sideLabels
        ).apply {
            isDraggable = true
        }

        //missing delete the old HexagonView from the BiMap and insert the modified one?

        //Put the new rotated tile to play
        playableTile[0, 0] = newHexagon
    }



    override fun refreshAfterTokenTilePairChosen() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        shopTokens.isVisible = false
        shopHabitats.isVisible = false
        playArea.isDisabled = false
        playableTile.isDisabled = false
        confirmReplacementButton.isDisabled = true
        resolveOverpopButton.isDisabled = true
        chooseCustomPair.isDisabled = true
        discardToken.isDisabled = true
        replaceWildlifeButton.isDisabled = true

        //call method getAllPossibleCoordinatesForTilePlacing in createPossibleHexagons
        createPossibleHexagons(listOf(Pair(1,1),Pair(-1,0),Pair(1,-2),Pair(-2,1),Pair(2,-1),Pair(0,1),Pair(0,-1)))


        playableToken[0,0] = selectedToken
        playableToken[0,0]?.onMouseClicked = null

        playableToken.isDisabled = true

        playableTile[0,0] = selectedHabitat?.apply {
            onMouseClicked = {
                //rootService.playerActionService.rotateTile()
            }
            isDisabled = false
        }
    }

    override fun refreshAfterWildlifeTokenAdded() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        super.refreshAfterWildlifeTokenAdded()
    }

    override fun refreshAfterWildlifeTokenReplaced() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        for(i in 0..3){
            //replace all tokens with new ones / reload
            println("Replaced")
        }
    }

    override fun refreshAfterNextTurn() {
        //val game = rootService.currentGame
        //checkNotNull(game)

        playArea.clear()
        enableAll()
        custom = false
        // iterate over all elements a player has and add it to playArea
        // change currentPlayerLabel
        //change NatureToken
        //fill shop with elements
        //waiting 3 secs?
    }

//    private fun disableAll() {
//        // Disable all components
//        listOf(
//            replaceWildlifeButton,
//            confirmReplacementButton,
//            chooseCustomPair,
//            resolveOverpopButton,
//            shopTokens,
//            shopHabitats,
//            playArea,
//        ).forEach { it.isDisabled = true }
//    }

    private fun enableAll() {
        // Disable all components
        listOf(
            replaceWildlifeButton,
            confirmReplacementButton,
            chooseCustomPair,
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

        val color1 = if (color == Color.RED) {
            Color.WHITE
        } else {
            Color.LIGHT_GRAY
        }

        return HexagonView(
            size = size,
            visual = CompoundVisual(
                ColorVisual(color1), // Background color of the hexagon
                *textVisuals.toTypedArray(), // Spread operator to include all text visuals
                //TextVisual("Salmon")
            )
        )
    }


    private val labeledHexagon1 = createLabeledHexagonView(
        color = Color.LIGHT_GRAY,
        labels = listOf("F", "F", "F", "E", "E", "E")
    )

    private val labeledHexagon2 = createLabeledHexagonView(
        color = Color.LIGHT_GRAY,
        labels = listOf("A", "F", "F", "E", "E", "E")
    )

    private val labeledHexagon3 = createLabeledHexagonView(
        color = Color.LIGHT_GRAY,
        labels = listOf("B", "F", "F", "E", "E", "E")
    )

    private val labeledHexagon4 = createLabeledHexagonView(
        color = Color.LIGHT_GRAY,
        labels = listOf("D", "F", "F", "E", "E", "E")
    )

    //for all possible Position to play the Hexagon, mark it
    private fun createPossibleHexagons(position : List<Pair<Int,Int>>) {
        for (i in position) {
            val hex = HexagonView(
                size = 75,
                visual = ColorVisual(Color.GRAY)
            )
            playArea[i.second, i.first] = hex.apply {
                onMouseClicked = {
                    selectedHabitatX = i.second
                    selectedHabitatY = i.first
                    rootService.playerActionService.addTileToHabitat(i)
                    disablePossibleHexagons(position)
                }
            }
        }
    }

    //after tile placed, remove the possible Hexagons
    private fun disablePossibleHexagons(position: List<Pair<Int, Int>>){
        for (i in position){
                playArea[i.second,i.first]?.isDisabled = true
        }
    }

    //get the correct images
    private fun getRuleSets(){
        //val game = rootService.currentGame
        //checkNotNull(game)

        //iterate over ruleset and update the pngs (visuals) based on
    }

    /**
     * [disableTokensExcept] disables all other Tokens except the chosen one
     * in the shop
     */
    private fun disableTokensExcept(pos : HexagonView){
        for (i in 0..3)
            if (shopTokens[i,0] != pos){
                shopTokens[i,0]?.isDisabled = true
            }
    }

    /**
     * [disableHabitatsExcept] disables all other Habitats except the chosen one
     * in the shop
     */
    private fun disableHabitatsExcept(pos :HexagonView){
        for (i in 0..3)
            if (shopHabitats[i,0] != pos){
                shopHabitats[i,0]?.isDisabled = true
            }
    }

    /**
     * [enableAllTokens] enable all Tokens to be chosen
     * in the shop
     */
    private fun enableAllTokens(){
        for (i in 0..3){
            shopTokens[i,0]?.isDisabled = false
        }
    }

    /**
     * [enableAllHabitats] enable all Habitats to be chosen
     * in the shop
     */
    private fun enableAllHabitats(){
        for (i in 0..3){
            shopHabitats[i,0]?.isDisabled = false
        }
    }

    private fun updateButtonStates(action: String) {
        when (action) {
            "replaceWildlife" -> {
                replaceWildlifeButton.isDisabled = true
                chooseCustomPair.isDisabled = true
                resolveOverpopButton.isDisabled = true
                shopTokens.isDisabled = false
                confirmReplacementButton.isDisabled = false
            }

            "chooseCustomPair" -> {
                replaceWildlifeButton.isDisabled = true
                chooseCustomPair.isDisabled = true
                resolveOverpopButton.isDisabled = true
                shopTokens.isDisabled = false
            }

            "default" -> {
                replaceWildlifeButton.isDisabled = true
                chooseCustomPair.isDisabled = true
                resolveOverpopButton.isDisabled = true
                shopTokens.isDisabled = true
                confirmReplacementButton.isDisabled = true
            }
        }
    }

}
