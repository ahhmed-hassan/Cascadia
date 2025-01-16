package service

import entity.*
import kotlin.test.*
import service.ScoringService.Companion.PlayerScore

/**
 * Test class for the [ScoringService] class to validate the functionality of the calculateScore method.
 * This class ensures that the scoring logic works as expected for different player habitats,
 * including animal scoring, terrain scoring, and nature token handling.
 */
class CalculateScoreTest {
    private val rootService = RootService()
    private val scoringService = ScoringService(rootService)

    /**
     * Sets up the initial game state before test.
     *
     * This method initializes a new game with two local players and configures their habitats
     * using helper methods for each player's setup.
     */
    @BeforeTest
    fun setUp() {
        rootService.gameService.startNewGame(
            mapOf(
                "Local_Player1" to PlayerType.LOCAL,
                "Local_Player2" to PlayerType.LOCAL
            ),
            listOf(false, false, false, false, false),
            false,
            false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val allHabitatTiles = rootService.gameService.getHabitatTiles()
        game.playerList[0].habitat.clear()
        game.playerList[1].habitat.clear()

        setupPlayer1Habitat(game.playerList[0], allHabitatTiles)
        setupPlayer2Habitat(game.playerList[1], allHabitatTiles)
    }

    /**
     * Tests the calculateScore method for a two-player scenario.
     *
     * This test initializes a game with two local players, sets up their habitats with
     * specific habitat tiles and wildlife tokens, and then calculates the scores
     * based on animal patterns, terrain connections, and nature tokens.
     */
    @Test
    fun testCalculateScore() {
        val result = scoringService.calculateScore()
        val expectedScores = mapOf(
            "Local_Player1" to PlayerScore(
                animalsScores = mapOf(
                    Animal.BEAR to 4,
                    Animal.SALMON to 2,
                    Animal.ELK to 2,
                    Animal.FOX to 8,
                    Animal.HAWK to 2
                ),
                ownLongestTerrainsScores = mapOf(
                    Terrain.FOREST to 7,
                    Terrain.MOUNTAIN to 0,
                    Terrain.PRAIRIE to 0,
                    Terrain.RIVER to 1,
                    Terrain.WETLAND to 1
                ),
                natureTokens = 2,
                longestAmongOtherPlayers = mapOf(
                    Terrain.FOREST to 2,
                    Terrain.MOUNTAIN to 1,
                    Terrain.PRAIRIE to 1,
                    Terrain.RIVER to 0,
                    Terrain.WETLAND to 2
                )
            ),
            "Local_Player2" to PlayerScore(
                animalsScores = mapOf(
                    Animal.BEAR to 0,
                    Animal.SALMON to 16,
                    Animal.ELK to 0,
                    Animal.FOX to 0,
                    Animal.HAWK to 2
                ),
                ownLongestTerrainsScores = mapOf(
                    Terrain.FOREST to 0,
                    Terrain.MOUNTAIN to 0,
                    Terrain.PRAIRIE to 0,
                    Terrain.RIVER to 7,
                    Terrain.WETLAND to 0
                ),
                natureTokens = 3,
                longestAmongOtherPlayers = mapOf(
                    Terrain.FOREST to 0,
                    Terrain.MOUNTAIN to 1,
                    Terrain.PRAIRIE to 1,
                    Terrain.RIVER to 2,
                    Terrain.WETLAND to 0
                )
            )
        )
        assertEquals(expectedScores, result)
    }

    /**
     * Configures Player 1's habitat with specific habitat tiles and wildlife tokens.
     *
     * @param player The player whose habitat is being configured.
     * @param allHabitatTiles The list of all available habitat tiles.
     */
    private fun setupPlayer1Habitat(player: Player, allHabitatTiles: List<HabitatTile>) {
        player.habitat[Pair(0, 0)] = allHabitatTiles[11].apply {
            wildlifeToken = WildlifeToken(Animal.FOX)
        }
        player.habitat[Pair(1, 0)] = allHabitatTiles[12].apply {
            wildlifeToken = WildlifeToken(Animal.FOX)
        }
        player.habitat[Pair(1, -1)] = allHabitatTiles[13].apply {
            wildlifeToken = WildlifeToken(Animal.ELK)
        }
        player.habitat[Pair(0, -1)] = allHabitatTiles[14].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        player.habitat[Pair(-1, 0)] = allHabitatTiles[15].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        player.habitat[Pair(-1, 1)] = allHabitatTiles[55].apply {
            wildlifeToken = WildlifeToken(Animal.HAWK)
            rotationOffset = 2
        }
        player.habitat[Pair(0, 1)] = allHabitatTiles[16].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.natureToken = 2
    }

    /**
     * Configures Player 2's habitat with specific habitat tiles and wildlife tokens.
     *
     * @param player The player whose habitat is being configured.
     * @param allHabitatTiles The list of all available habitat tiles.
     */
    private fun setupPlayer2Habitat(player: Player, allHabitatTiles: List<HabitatTile>) {
        player.habitat[Pair(0, 0)] = allHabitatTiles[35].apply {
            wildlifeToken = WildlifeToken(Animal.HAWK)
        }
        player.habitat[Pair(1, 0)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.habitat[Pair(1, -1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.habitat[Pair(0, -1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.habitat[Pair(-1, 0)] = allHabitatTiles[37].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        player.habitat[Pair(-1, 1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.habitat[Pair(0, 1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        player.natureToken = 3
    }
}