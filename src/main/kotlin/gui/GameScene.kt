package gui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual

class GameScene : BoardGameScene(1920, 1080) {

    private val currentPlayerLabel = Label(
        width = 200, height = 50,
        posX = 50, posY = 20,
        text = "Current Player",
        visual = ColorVisual(255, 255, 255)
    )

    private val natureTokenLabel = Label(
        width = 200, height = 50,
        posX = 50, posY = 80,
        text = "NatureToken : 3",
        visual = ColorVisual(255, 255, 255)
    )

    private val replaceWildlifeButton = Button(
        width = 200, height = 50,
        posX = 50, posY = 200,
        text = "Replace Wildlife",
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val confirmReplacementButton = Button(
        width = 200, height = 50,
        posX = 50, posY = 270,
        text = "Confirm Replacement",
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val resolveOverpopButton = Button(
        width = 200, height = 50,
        posX = 1670, posY = 930,
        text = "Resolve Overpop",
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }

    private val showRuleSetButton = Button(
        width = 200, height = 50,
        posX = 1670, posY = 1000,
        text = "Show RuleSet",
        visual = ColorVisual(200, 150, 255)
    ).apply {
        onMouseClicked = {
        }
    }


    init {
        background = ColorVisual(240, 240, 180)

        addComponents(
            currentPlayerLabel,
            natureTokenLabel,
            replaceWildlifeButton,
            confirmReplacementButton,
            resolveOverpopButton,
            showRuleSetButton,
        )

    }
}
