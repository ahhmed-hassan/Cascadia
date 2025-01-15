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
     * Tests the calculateScore method for a two-player scenario.
     *
     * This test initializes a game with two local players, sets up their habitats with
     * specific habitat tiles and wildlife tokens, and then calculates the scores
     * based on animal patterns, terrain connections, and nature tokens.
     */
    @Test
    fun testCalculateScore() {
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

        // Setup Player 1's habitat
        game.playerList[0].habitat[Pair(0, 0)] = allHabitatTiles[11].apply {
            wildlifeToken = WildlifeToken(Animal.FOX)
        }
        game.playerList[0].habitat[Pair(1, 0)] = allHabitatTiles[12].apply {
            wildlifeToken = WildlifeToken(Animal.FOX)
        }
        game.playerList[0].habitat[Pair(1, -1)] = allHabitatTiles[13].apply {
            wildlifeToken = WildlifeToken(Animal.ELK)
        }
        game.playerList[0].habitat[Pair(0, -1)] = allHabitatTiles[14].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        game.playerList[0].habitat[Pair(-1, 0)] = allHabitatTiles[15].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        game.playerList[0].habitat[Pair(-1, 1)] = allHabitatTiles[55].apply {
            wildlifeToken = WildlifeToken(Animal.HAWK)
            rotationOffset = 2
        }
        game.playerList[0].habitat[Pair(0, 1)] = allHabitatTiles[16].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[0].natureToken = 2

        // Setup Player 2s habitat
        game.playerList[1].habitat[Pair(0, 0)] = allHabitatTiles[35].apply {
            wildlifeToken = WildlifeToken(Animal.HAWK)
        }
        game.playerList[1].habitat[Pair(1, 0)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[1].habitat[Pair(1, -1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[1].habitat[Pair(0, -1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[1].habitat[Pair(-1, 0)] = allHabitatTiles[37].apply {
            wildlifeToken = WildlifeToken(Animal.BEAR)
        }
        game.playerList[1].habitat[Pair(-1, 1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[1].habitat[Pair(0, 1)] = allHabitatTiles[34].apply {
            wildlifeToken = WildlifeToken(Animal.SALMON)
        }
        game.playerList[1].natureToken = 3

        println(game.playerList[0].habitat)
        println(game.playerList[1].habitat)

        val result = scoringService.calculateScore()

        //Expected scores
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
        println(result)
        assertEquals(expectedScores, result)
    }
}