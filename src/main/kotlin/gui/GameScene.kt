package gui

import entity.Animal
import entity.HabitatTile
import entity.PlayerType
import entity.Terrain
import service.RootService
import tools.aqua.bgw.animation.DelayAnimation
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

class GameScene(
    val rootService: RootService,
    private val hotSeatConfigurationMenuScene: HotSeatConfigurationMenuScene,
    private val networkJoinMenuScene: NetworkJoinMenuScene,
    private val networkConfigurationMenuScene: NetworkConfigurationMenuScene
) : BoardGameScene(1920, 1080), Refreshables {

    private val habitats: BidirectionalMap<HabitatTile, HexagonView> = BidirectionalMap()
    private var selectedHabitat: Int = 0
    private var selectedToken: Int = 0
    private var selectedHabitatX: Int = 0
    private var selectedHabitatY: Int = 0
    private var selectedShopToken: MutableList<Int> = mutableListOf()
    private var custom: Boolean = false
    private var currentXCamera = 0
    private var currentYCamera = 0
    private var speed = 0

    private val shopHabitats = GridPane<HexagonView>(
        posX = 1400,
        posY = 100,
        rows = 1,
        columns = 4,
        spacing = 50,
        visual = ColorVisual(Color.GRAY)
    )

    private val shopTokens = GridPane<HexagonView>(
        posX = 1400,
        posY = 250,
        rows = 1,
        columns = 4,
        spacing = 100,
        visual = ColorVisual(Color.GRAY)
    )

    private val playArea = HexagonGrid<HexagonView>(
        posX = 1920 / 2 - 100,
        posY = 1080 / 2 - 100,
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

    private val targetLayout = Pane<ComponentView>(width = 1920, height = 1080)
    private val cameraPane = CameraPane(
        width = 1920,
        height = 1080,
        target = targetLayout
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
            for (i in 0..3) {
                shopTokens[i, 0]?.onMouseClicked = { mouseEvent ->
                    when (mouseEvent.button) {
                        MouseButtonType.LEFT_BUTTON -> {
                            shopTokens[i, 0]?.apply {
                                posY -= 25
                            }
                            selectedShopToken.add(i)
                        }

                        MouseButtonType.RIGHT_BUTTON -> {
                            shopTokens[i, 0]?.apply {
                                posY = 0.01
                            }
                            selectedShopToken.remove(i)
                        }

                        else -> {}
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
            val game = rootService.currentGame
            checkNotNull(game)
            if (custom) {
                rootService.playerActionService.chooseCustomPair(selectedHabitat, selectedToken)
                enableAllHabitats()
                natureTokenLabel.apply { text = "NatureToken: " + game.currentPlayer.natureToken.toString() }
                custom = false
            } else {
                rootService.playerActionService.replaceWildlifeTokens(selectedShopToken)
                natureTokenLabel.apply { text = "NatureToken: " + game.currentPlayer.natureToken.toString() }

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
                shopTokens[i, 0]?.onMouseClicked = { mouseEvent ->
                    when (mouseEvent.button) {
                        MouseButtonType.RIGHT_BUTTON -> {
                            shopTokens[i, 0]?.apply {
                                posY = 0.01
                            }
                            enableAllTokens()
                        }

                        MouseButtonType.LEFT_BUTTON -> {
                            shopTokens[i, 0]?.apply {
                                posY -= 25
                            }
                            selectedToken = i
                            disableTokensExcept(checkNotNull(shopTokens[i, 0]))
                        }

                        else -> {}
                    }
                }

                shopHabitats[i, 0]?.onMouseClicked = { mouseEvent ->
                    when (mouseEvent.button) {
                        MouseButtonType.RIGHT_BUTTON -> {
                            shopHabitats[i, 0]?.apply {
                                posY = 0.01
                            }
                            enableAllHabitats()
                        }

                        MouseButtonType.LEFT_BUTTON -> {
                            shopHabitats[i, 0]?.apply {
                                posY -= 25
                            }
                            selectedHabitat = i
                            disableHabitatsExcept(checkNotNull(shopHabitats[i, 0]))
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
            rootService.playerActionService.replaceWildlifeTokens(hasThreeSameWildlifeTokens().second)
            this.isDisabled = true
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
        height = 270
    )

    private val elkRule = Label(
        posX = 520,
        posY = 50,
        width = 420,
        height = 270
    )

    private val foxRule = Label(
        posX = 990,
        posY = 50,
        width = 420,
        height = 270
    )

    private val hawkRule = Label(
        posX = 50,
        posY = 370,
        width = 420,
        height = 270
    )

    private val salmonRule = Label(
        posX = 520,
        posY = 370,
        width = 420,
        height = 270
    )


    init {
        background = ColorVisual(240, 240, 180)

        onKeyPressed = {
            //go left with camera
            if (it.keyCode == KeyCode.A) {
                currentXCamera += -100
                cameraPane.reposition(currentXCamera, currentYCamera)
            }

            //go up with camera
            if (it.keyCode == KeyCode.W) {
                currentYCamera += -100
                cameraPane.reposition(currentXCamera, currentYCamera)
            }

            //go right with camera
            if (it.keyCode == KeyCode.D) {
                currentXCamera += 100
                cameraPane.reposition(currentXCamera, currentYCamera)
            }

            //go down with camera
            if (it.keyCode == KeyCode.S) {
                currentYCamera += 100
                cameraPane.reposition(currentXCamera, currentYCamera)
            }

            //reset the pane to original position
            if (it.keyCode == KeyCode.R) {
                cameraPane.reposition(0, 0)
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
        val game = rootService.currentGame
        checkNotNull(game)
        println("RefreshStart")

        speed = hotSeatConfigurationMenuScene.getSpeed().toInt() * 1000

        //add the playArea to the CameraPane
        targetLayout.add(playArea)

        getRuleSets()

        ruleSetOverlay.addAll(
            closeRuleSet,
            bearRule,
            elkRule,
            foxRule,
            hawkRule,
            salmonRule
        )

        //create a HexagonView for each tile
        for (habitat in game.habitatTileList)
            habitats[habitat] = createLabeledHexagonView(
                color = habitat.isKeystoneTile,
                labels = habitat.terrains.map { terrain: Terrain ->
                    (terrain.name.substring(0, 1))
                },
                tokens = habitat.wildlifeSymbols.map { animal: Animal -> animal.name.substring(0, 1) }
            )

        //create a HexagonView for each shopTile
        for (habitat in game.shop) {
            val habitate = checkNotNull(habitat.first)
            habitats[habitate] = createLabeledHexagonView(
                color = habitate.isKeystoneTile,
                labels = habitate.terrains.map { terrain: Terrain ->
                    (terrain.name.substring(0, 1))
                },
                tokens = habitate.wildlifeSymbols.map { animal: Animal -> animal.name.substring(0, 1) }
            )
        }

        //create a HexagonView for each starter Tile
        for (player in game.startTileList) {
            for (habitate in player) {
                habitats[habitate] = createLabeledHexagonView(
                    color = habitate.isKeystoneTile,
                    labels = habitate.terrains.map { terrain: Terrain ->
                        (terrain.name.substring(0, 1))
                    },
                    tokens = habitate.wildlifeSymbols.map { animal: Animal -> animal.name.substring(0, 1) }
                )
            }
        }

        //add the views to the shop
        for (i in 0..3) {
            shopHabitats[i, 0] = habitats[game.shop[i].first!!] as HexagonView
            //create HexagonViews for the Token
            shopTokens[i, 0] = game.shop[i].second?.animal?.let { createTokens(it.name) }
        }

        //If player Clicks on any Habitat or Token that vertical pair is chosen
        for (i in 0..3) {
            shopTokens[i, 0]?.apply {
                onMouseClicked = {
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
            shopHabitats[i, 0]?.apply {
                onMouseClicked = {
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
        }

        //add all habitats for the starting player to the playField
        for (habitat in game.currentPlayer.habitat) {
            playArea[habitat.key.second, habitat.key.first] = habitats[habitat.value] as HexagonView
        }

        discardToken.isDisabled = true

        //Update Labels for name and NatureToken
        natureTokenLabel.apply { text = "NatureToken: " + game.currentPlayer.natureToken.toString() }
        currentPlayerLabel.apply { text = game.currentPlayer.name }

        //Disable resolveOverpopulation if already done or not possible
        if (game.hasReplacedThreeToken || !hasThreeSameWildlifeTokens().first) {
            resolveOverpopButton.isDisabled = true
        } else
            resolveOverpopButton.isDisabled = false

        //Disable Buttons for action with NatureToken if player has none
        if (game.currentPlayer.natureToken == 0) {
            chooseCustomPair.isDisabled = true
            replaceWildlifeButton.isDisabled = true
            confirmReplacementButton.isDisabled = true
        }

        if (game.currentPlayer.playerType == PlayerType.EASY) {
            disableAll()
            playAnimation(DelayAnimation(speed).apply {
                onFinished = {
                    rootService.easyBotService.takeTurn()
                }
            })
        }
    }

    override fun refreshAfterHabitatTileAdded() {
        val game = rootService.currentGame
        checkNotNull(game)
        println("RefreshTileAdd")

        playableTile[0, 0] = null
        playArea.clear()

        //add all habitats for current Player to the playField
        for (habitat in game.currentPlayer.habitat) {
            playArea[habitat.key.second, habitat.key.first] = (habitats[habitat.value] as HexagonView).apply {
                onMouseClicked = null
            }
        }

        playableTile[0, 0]?.isDisabled = true

        playableToken.isDisabled = false
        //if (game.currentPlayer.playerType == PlayerType.LOCAL)
        discardToken.isDisabled = false

        //get the List of all Habitats where selected Token can be placed
        val tokenHabitate = game.selectedToken?.let {
            rootService.gameService.getAllPossibleTilesForWildlife(
                it.animal,
                habitat = game.currentPlayer.habitat
            )
        }

        //enable for each Habitat where Token can be put onMouseClick
        for (habitat in game.currentPlayer.habitat) {
            if (habitat.value in checkNotNull(tokenHabitate)){// && game.currentPlayer.playerType == PlayerType.LOCAL) {
                playArea[habitat.key.second, habitat.key.first] = (habitats[habitat.value] as HexagonView).apply {
                    isDisabled = false
                    onMouseClicked = {
                        rootService.playerActionService.addToken(habitat.value)
                    }
                }
            }
        }

        println("RefreshTileAdd")
    }

    override fun refreshAfterTileRotation() {
        val game = rootService.currentGame
        checkNotNull(game)

        val rotateTile = game.selectedTile
        checkNotNull(rotateTile)

        // get the list of terrains from Habitat
        val sideLabels = rotateTile.terrains.map { terrain: Terrain -> terrain.name.substring(0, 1) }

        // create new Hexagon for the rotated Tile
        val newHexagon = createLabeledHexagonView(
            labels = sideLabels,
            color = rotateTile.isKeystoneTile,
            tokens = rotateTile.wildlifeSymbols.map { animal: Animal -> animal.name.substring(0, 1) }
        ).apply {
            onMouseClicked = {
                rootService.playerActionService.rotateTile()
            }
        }

        habitats[rotateTile] = newHexagon

        playableTile[0, 0] = null
        playableTile[0, 0] = habitats[rotateTile] as HexagonView

        println("RefreshAfterTileRotation")
    }

    override fun refreshAfterTokenTilePairChosen() {
        val game = rootService.currentGame
        checkNotNull(game)

        shopTokens.isVisible = false
        shopHabitats.isVisible = false
        shopTokens.isDisabled = true
        shopHabitats.isDisabled = true
        playArea.isDisabled = false
        playableTile.isDisabled = false
        confirmReplacementButton.isDisabled = true
        resolveOverpopButton.isDisabled = true
        chooseCustomPair.isDisabled = true
        discardToken.isDisabled = true
        replaceWildlifeButton.isDisabled = true

        //call method getAllPossibleCoordinatesForTilePlacing in createPossibleHexagons
        createPossibleHexagons(rootService.gameService.getAllPossibleCoordinatesForTilePlacing(game.currentPlayer.habitat))

        val tokenToPlay = game.selectedToken
        checkNotNull(tokenToPlay)

        //put the selected Token to left Bottom
        playableToken[0, 0] = createTokens(tokenToPlay.animal.name)
        playableToken[0, 0]?.onMouseClicked = null

        playableToken.isDisabled = true

        //put the selected Habitat to the left Bottom
        val tileToPlay = game.selectedTile
        checkNotNull(tileToPlay)
        println(tileToPlay)
        playableTile[0, 0] = (habitats[tileToPlay] as HexagonView).apply {
            onMouseClicked = {
                rootService.playerActionService.rotateTile()
            }
            isDisabled = false
        }

        println("RefreshChosen")
    }

    override fun refreshAfterWildlifeTokenAdded(habitatTile: HabitatTile) {
        val game = rootService.currentGame
        checkNotNull(game)

        //update the view of the Habitat where we placed our token
        playableToken[0, 0] = null
        habitats[habitatTile] = createLabeledHexagonView(
            color = habitatTile.isKeystoneTile,
            labels = habitatTile.terrains.map { terrain: Terrain -> terrain.name.substring(0, 1) },
            token = habitatTile.wildlifeToken?.animal.toString().substring(0, 1)
        )

        println("RefreshToken")
    }

    override fun refreshAfterWildlifeTokenReplaced() {
        val game = rootService.currentGame
        checkNotNull(game)

        //add the views to the shop
        for (i in 0..3) {
            //create HexagonViews for the Token
            shopTokens[i, 0] = game.shop[i].second?.animal?.let { createTokens(it.name) }
            shopTokens[i, 0]?.apply {
                onMouseClicked = {
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
        }
        selectedShopToken.clear()
    }

    override fun refreshAfterNextTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        //reset the camera to original position
        cameraPane.reposition(0, 0)
        currentXCamera = 0
        currentYCamera = 0

        showRuleSetButton.isDisabled = false
        cameraPane.isDisabled = false
        shopTokens.isDisabled = false
        shopHabitats.isDisabled = false
        shopTokens.isVisible = true
        shopHabitats.isVisible = true

        playableToken[0, 0] = null

        //add the views to the shop
        for (i in 0..3) {
            shopHabitats[i, 0] = habitats[game.shop[i].first!!] as HexagonView
            //create HexagonViews for the Token
            shopTokens[i, 0] = game.shop[i].second?.animal?.let { createTokens(it.name) }
        }

        //If player Clicks on any Habitat or Token that vertical pair is chosen
        for (i in 0..3) {
            shopTokens[i, 0]?.apply {
                onMouseClicked = {
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
            shopHabitats[i, 0]?.apply {
                onMouseClicked = {
                    rootService.playerActionService.chooseTokenTilePair(i)
                }
            }
        }

        playArea.clear()

        //add all habitats for the starting player to the playField
        for (habitat in game.currentPlayer.habitat) {
            playArea[habitat.key.second, habitat.key.first] = habitats[habitat.value] as HexagonView
        }

        discardToken.isDisabled = true

        //Update Labels for name and NatureToken
        natureTokenLabel.apply { text = "NatureToken: " + game.currentPlayer.natureToken.toString() }
        currentPlayerLabel.apply { text = game.currentPlayer.name }

        //Disable resolveOverpopulation if already done or not possible
        if (game.hasReplacedThreeToken || !hasThreeSameWildlifeTokens().first) {
            resolveOverpopButton.isDisabled = true
        } else
            resolveOverpopButton.isDisabled = false

        //Disable Buttons for action with NatureToken if player has none
        if (game.currentPlayer.natureToken == 0) {
            chooseCustomPair.isDisabled = true
            replaceWildlifeButton.isDisabled = true
            confirmReplacementButton.isDisabled = true
        } else {
            chooseCustomPair.isDisabled = false
            replaceWildlifeButton.isDisabled = false
            confirmReplacementButton.isDisabled = false
        }


        if (game.currentPlayer.playerType == PlayerType.EASY) {
            disableAll()
            playAnimation(DelayAnimation(speed).apply {
                onFinished = {
                    rootService.easyBotService.takeTurn()
                }
            })
        }
        println(game.ruleSet)
        println("RefreshNext")
    }

    /**
     * [createPossibleHexagons] creates the HexagonView based on the Information of the HabitatTile
     *
     * @param color is true if the HabitatTile is a Keystone
     * @param labels is the list of terrains
     * @param tokens list of possible Animals to place
     * @param token is the Token placed on this Tile
     */
    private fun createLabeledHexagonView(
        color: Boolean,
        labels: List<String>,
        tokens: List<String> = emptyList(),
        token: String = ""
    ): HexagonView {

        val size = 75

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

        // if is keystone than it gets color White else Light Gray
        val color1 = if (color) {
            Color.WHITE
        } else {
            Color.LIGHT_GRAY
        }

        //if there is no Token placed on this tile, then we can see the possible Animals
        val tokenText = token.ifEmpty {
            tokens.toString()
        }
        return HexagonView(
            size = size,
            visual = CompoundVisual(
                ColorVisual(color1), // Background color of the hexagon
                *textVisuals.toTypedArray(), // Spread operator to include all text visuals
                TextVisual(tokenText)
            )
        )
    }

    /**
     * [createTokens] creates a HexagonView based on the AnimalName
     *
     * @param animal the string of the Animal
     */
    private fun createTokens(animal: String): HexagonView {
        return HexagonView(
            size = 50,
            visual = CompoundVisual(
                ColorVisual(Color.LIGHT_GRAY),
                TextVisual(animal)
            )
        )
    }

    /**
     * [createPossibleHexagons] for given positions where we can play the Habitat, we mark them so
     * onMouseClick we can place the Habitat
     *
     * @param position List of possible Positions
     */
    private fun createPossibleHexagons(position: List<Pair<Int, Int>>) {
        val game = rootService.currentGame
        checkNotNull(game)

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
                }
            }
//            if (game.currentPlayer.playerType != PlayerType.LOCAL) {
//                playArea[i.second, i.first]?.onMouseClicked = null
//            }
        }
    }

    /**
     * [getRuleSets] loads the correct images for ruleSets of the current game
     */
    private fun getRuleSets() {
        val game = rootService.currentGame
        checkNotNull(game)

        if (!game.ruleSet[0]) {
            bearRule.visual = ImageVisual("Bear_A.png")
        } else
            bearRule.visual = ImageVisual("Bear_B.png")

        if (!game.ruleSet[1]) {
            elkRule.visual = ImageVisual("Elk_A.png")
        } else
            elkRule.visual = ImageVisual("Elk_B.png")

        if (!game.ruleSet[2]) {
            foxRule.visual = ImageVisual("Fox_A.png")
        } else
            foxRule.visual = ImageVisual("Fox_B.png")

        if (!game.ruleSet[3]) {
            hawkRule.visual = ImageVisual("Hawk_A.png")
        } else
            hawkRule.visual = ImageVisual("Hawk_B.png")

        if (!game.ruleSet[4]) {
            salmonRule.visual = ImageVisual("Salmon_A.png")
        } else
            salmonRule.visual = ImageVisual("Salmon_B.png")
    }

    /**
     * [disableTokensExcept] disables all other Tokens except the chosen one
     * in the shop
     */
    private fun disableTokensExcept(pos: HexagonView) {
        for (i in 0..3)
            if (shopTokens[i, 0] != pos) {
                shopTokens[i, 0]?.isDisabled = true
            }
    }

    /**
     * [disableHabitatsExcept] disables all other Habitats except the chosen one
     * in the shop
     */
    private fun disableHabitatsExcept(pos: HexagonView) {
        for (i in 0..3)
            if (shopHabitats[i, 0] != pos) {
                shopHabitats[i, 0]?.isDisabled = true
            }
    }

    /**
     * [enableAllTokens] enable all Tokens to be chosen
     * in the shop
     */
    private fun enableAllTokens() {
        for (i in 0..3) {
            shopTokens[i, 0]?.isDisabled = false
        }
    }

    /**
     * [enableAllHabitats] enable all Habitats to be chosen
     * in the shop
     */
    private fun enableAllHabitats() {
        for (i in 0..3) {
            shopHabitats[i, 0]?.isDisabled = false
        }
    }

    /**
     * [hasThreeSameWildlifeTokens] checks if hte shop has three equal tokens.
     *
     * @return Boolean if there are three equal token and the list of their position
     */
    private fun hasThreeSameWildlifeTokens(): Pair<Boolean, List<Int>> {
        val game = rootService.currentGame
        checkNotNull(game)
        // Map to store the indices of WildlifeTokens grouped by type
        val tokenIndexMap = mutableMapOf<Animal, MutableList<Int>>()

        // Group WildlifeTokens by their type, keeping track of their indices
        game.shop.forEachIndexed { index, pair ->
            val token = pair.second // Access the WildlifeToken
            if (token != null) {
                tokenIndexMap.computeIfAbsent(token.animal) { mutableListOf() }.add(index)
            }
        }

        // Find the first group with at least 3 tokens
        val matchingGroup = tokenIndexMap.entries.firstOrNull { it.value.size >= 3 }
        return if (matchingGroup != null) {
            Pair(true, matchingGroup.value.take(3)) // Return true and the first 3 indices
        } else {
            Pair(false, emptyList()) // No match, return false and an empty list
        }
    }

    /**
     * [updateButtonStates] update which Buttons need to be enabled or disabled based on action
     */
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
                confirmReplacementButton.isDisabled = true
            }
        }
    }

    private fun disableAll() {
        listOf(
            //cameraPane,
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
        ).onEach { it.isDisabled = true }
    }
}