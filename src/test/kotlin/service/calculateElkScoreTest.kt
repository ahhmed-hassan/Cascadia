package service
import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


class calculateElkScoreTest {
    private var rootService = RootService()
    private var gameService = GameService(rootService)
    private var playerActionService = PlayerActionService(rootService)
    val game = rootService.currentGame

    @Test
    fun testStraightLine1() {
        gameService.startNewGame(mapOf("Alice" to PlayerType.LOCAL), listOf(true, false, true, false, true))
        playerActionService.addTileToHabitat(Pair<>)
    }
}