package service

import entity.PlayerType
import kotlin.test.*

/**
 * This test class validates the functionality of [service.GameService.startNewGame], ensuring that the game is
 * properly initialized and is working for further use.
 */
class StartNewGameTest {
    private var rootService = RootService()

    /**
     * Sets up a new root service and starts a new game before each test
     * Initializes the game with two local players and default rules
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        rootService.networkService.updateConnectionState(ConnectionState.DISCONNECTED)
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
            ),
            listOf(false, false, false, false, false), //Rule A
            false,
            false,
            null
        )
    }

    /**
     * Test starting a new game with valid parameters
     */
    @Test
    fun testStartNewGame() {
        val game = rootService.currentGame
        assertNotNull(game)

        // Verify the Hotseat Mode
        assertEquals(ConnectionState.DISCONNECTED, rootService.networkService.connectionState)
        assertNotEquals(ConnectionState.CONNECTED, rootService.networkService.connectionState)

        // Verify the number of players, names and types
        assertEquals(2, game.playerList.size)
        assertEquals("Local_Player1", game.playerList[0].name)
        assertEquals("Local_Player2", game.playerList[1].name)
        assertEquals(game.playerList[0].playerType, PlayerType.LOCAL)
        assertEquals(game.playerList[1].playerType, PlayerType.LOCAL)

        //Verify current player
        assertEquals(game.playerList[0], game.currentPlayer)

        // Verify the scoring rules
        assertEquals(5, game.ruleSet.size)
        assertTrue(game.ruleSet.all { it == false })

        // Verify shop is initialized correctly
        assertEquals(4, game.shop.size)
        assertTrue(game.shop.all { it.first != null && it.second != null })

        //Verify starting tiles
        assertEquals(5, game.startTileList.size)
        val p1StartingTile = game.startTileList[0]
        val p2StartingTile = game.startTileList[1]

        val totalStartingTilesInGame = p1StartingTile + p2StartingTile
        assertEquals(6, totalStartingTilesInGame.size) // 2 start tiles (each 3 tiles)

        //Verify nature token count
        assertEquals(25, game.natureToken)

        //Verify wildlife tokens
        assertTrue(game.wildlifeTokenList.isNotEmpty())
        assertEquals(96, game.wildlifeTokenList.size) //4 Tokens are in shop

        //Verify Habitat Tiles
        assertEquals(39, game.habitatTileList.size) // for 2 Players, 4 Tiles are in shop

        //Verify simulation speed
        assertEquals(0.3f, game.simulationSpeed)

        //hasReplacedThreeToken musst be false
        assertFalse(game.hasReplacedThreeToken)

        //hasPlayedTile musst be false
        assertFalse(game.hasPlayedTile)

        //selected Tile and Token musst be null
        assertNull(game.selectedTile)
        assertNull(game.selectedToken)
    }

    /**
     * Verifies that starting a new game with 3 players initializes the correct number of habitat tiles.
     */
    @Test
    fun testStartNewGameWith3Players() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL,
                "Easy_Bot" to PlayerType.EASY
            ),
            listOf(false, false, false, false, false), //Rule A
            false,
            false,
            null
        )

        val game = rootService.currentGame
        assertNotNull(game)

        //Verify Habitat Tiles
        assertEquals(59, game.habitatTileList.size) // for 3 Players, 4 Tiles are in shop
    }

    /**
     * Verifies that starting a new game with 4 players initializes the correct number of habitat tiles.
     */
    @Test
    fun testStartNewGameWith4Players() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL,
                "Easy_Bot" to PlayerType.EASY,
                "Normal_Bot" to PlayerType.NORMAL
            ),
            listOf(false, false, false, false, false), //Rule A
            false,
            false,
            null
        )

        val game = rootService.currentGame
        assertNotNull(game)

        //Verify Habitat Tiles
        assertEquals(79, game.habitatTileList.size) // for 4 Players, 4 Tiles are in shop
    }

    /**
     * Verifies that when "orderIsRandom" is true, the player order is randomized.
     */
    @Test
    fun testPlayerOrderIsRandom() {
        // Define the players with their respective types
        val playerNames = mapOf(
            "Local_Player1" to PlayerType.LOCAL,
            "Local_Player2" to PlayerType.LOCAL,
            "Easy_Bot1" to PlayerType.EASY,
            "Easy_Bot2" to PlayerType.EASY
        )

        // Start a new game with random player order
        rootService.gameService.startNewGame(
            playerNames,
            listOf(false, false, false, false, false), //Rule A
            true,
            false,
            null
        )
        val game = rootService.currentGame
        assertNotNull(game)

        // Get the randomized player order and the original order
        val randomOrder = game.playerList.map { it.name }
        val originalOrder = playerNames.keys.toList()

        // Assert that the random order is different from the original order
        assertNotEquals(originalOrder, randomOrder)
    }

    /**
     * Verifies that when "isRandomRules" is true, the rule set is randomized.
     */
    @Test
    fun testRuleSetIsRandom() {
        // Define the rule set
        val ruleSet = listOf(false, false, false, false, false)

        // Start a new game with random rule set
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL,
                "Easy_Bot1" to PlayerType.EASY,
                "Easy_Bot2" to PlayerType.EASY
            ),
            ruleSet,
            false,
            true,
            null
        )
        val game = rootService.currentGame
        assertNotNull(game)

        // Get the randomized rule set
        val randomRuleSet = game.ruleSet

        // Assert that the random rule set is different from the original set
        assertNotEquals(ruleSet, randomRuleSet)
    }

    /**
     * This test verifies that starting a game with 5 players throws an IllegalArgumentException
     */
    @Test
    fun testStartNewGameWithFivePlayers() {
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.startNewGame(
                mapOf(
                    "Local_Player1" to PlayerType.LOCAL,
                    "Local_Player2" to PlayerType.LOCAL,
                    "Local_Player3" to PlayerType.LOCAL,
                    "Local_Player4" to PlayerType.LOCAL,
                    "Local_Player5" to PlayerType.LOCAL
                ),
                listOf(false, false, false, false, false),
                false,
                false,
                null
            )
        }
    }

    /**
     * This test verifies that starting a game with duplicate player names throws an IllegalArgumentException
     */
    @Test
    fun testDuplicatePlayerNames() {
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.startNewGame(
                mapOf(
                    "Local_Player" to PlayerType.LOCAL,
                    "Local_Player" to PlayerType.LOCAL
                ),
                listOf(false, false, false, false, false),
                false,
                false,
                null
            )
        }
    }

    /**
     * This test verifies that starting a hotseat game with a network player throws an IllegalArgumentException
     */
    @Test
    fun testNetworkGameWithoutLocalPlayer() {
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.startNewGame(
                mapOf(
                    "Local_Player" to PlayerType.LOCAL,
                    "Network_Player" to PlayerType.NETWORK,
                    "Easy_Bot" to PlayerType.EASY,
                    "Normal_Bot" to PlayerType.NORMAL
                ),
                listOf(false, false, false, false, false),
                false,
                false,
                null
            )
        }
    }

    /**
     * This test verifies that starting a game with scoring rules list that does not have exactly 5 entries
     */
    @Test
    fun testScoringRulesEntryCount() {
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.startNewGame(
                mapOf(
                    "Local_Player1" to PlayerType.LOCAL,
                    "Network_Player1" to PlayerType.NETWORK,
                    "Network_Player2" to PlayerType.NETWORK,
                    "Network_Player3" to PlayerType.NETWORK
                ),
                listOf(false, false, false),
                false,
                false,
                null
            )
        }
    }

    /**
     * This test verifies the game state immediately after it is created:
     * 1. Validate that the starting tiles are correctly placed in each player's habitat.
     */
    @Test
    fun testStartTilesPlacement() {
        // Retrieve the current game instance
        val game = rootService.currentGame
        assertNotNull(game)

        // Validate the starting tiles placement for each player
        game.playerList.forEachIndexed { index, player ->
            val playerStartTiles = game.startTileList[index]

            // Check that the tiles are placed in the correct positions in the player's habitat
            assertEquals(player.habitat[0 to 0], playerStartTiles[0]) //The top tile should match the starting tile
            assertEquals(player.habitat[1 to -1], playerStartTiles[1]) //The lower-right tile should match...
            assertEquals(player.habitat[1 to 0], playerStartTiles[2]) //The lower-left tile should match...
        }

        println(game.playerList[0].habitat.toString())
        println(game.playerList[1].habitat.toString())
    }

    /**
     * Verifies that there is no overpopulation in the shop immediately after the game is created.
     */
    @Test
    fun testOverpopulationAfterGameCreated() {
        // Retrieve the current game instance
        val game = rootService.currentGame
        assertNotNull(game)

        // Group the wildlife tokens in the shop by animal type
        val groupedAnimals = game.shop.groupBy { it.second?.animal }

        // Ensure no animal type appears more than 3 times in the shop
        assertTrue(groupedAnimals.all { it.value.size < 4 })

        println(game.shop.map { it.second?.animal })
    }
}