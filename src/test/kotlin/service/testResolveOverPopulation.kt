package service

import entity.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import kotlin.test.*

class testResolveOverPopulation {

    @Test
    fun testResolveOverPopulation() {
        val rootService = RootService()
        val gameService = GameService(rootService)
        gameService.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL,
                "Bob" to PlayerType.EASY),
            listOf(true, false, true, false, true),
            false,
            false,
            null
        )
        val game = rootService.currentGame
        checkNotNull(game)
        game.shop.clear()
        val tile1 = HabitatTile( 1, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.FOREST)
        )
        val tile2 = HabitatTile( 2, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.RIVER)
        )
        val tile3 = HabitatTile( 3, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.PRAIRIE)
        )
        val tile4 = HabitatTile( 4, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.PRAIRIE)
        )
        val token1 = WildlifeToken(Animal.FOX)
        val token2 = WildlifeToken(Animal.FOX)
        val token3 = WildlifeToken(Animal.FOX)
        val token4 = WildlifeToken(Animal.FOX)

        game.shop.add(Pair(tile1, token1))
        game.shop.add(Pair(tile2, token2))
        game.shop.add(Pair(tile3, token3))
        game.shop.add(Pair(tile4, token4))
        val originalShop = game.shop.map { it.copy() }

        gameService.resolveOverpopulation()

        assertNotEquals(originalShop, game.shop,
            "the Shop remained the same after resolveOverpopulation.")


    }
    @Test
    fun testCheckForSameAnimal() {
        val rootService = RootService()
        val gameService = GameService(rootService)
        gameService.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL,
                "Bob" to PlayerType.EASY),
            listOf(true, false, true, false, true),
            false,
            false,
            null
        )

        val game = rootService.currentGame
        checkNotNull(game)

        game.shop.clear()

        val tile1 = HabitatTile(1, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.FOREST))
        val tile2 = HabitatTile(2, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.RIVER))
        val tile3 = HabitatTile(3, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.PRAIRIE))
        val tile4 = HabitatTile(4, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.PRAIRIE))

        val token1 = WildlifeToken(Animal.FOX)
        val token2 = WildlifeToken(Animal.FOX)
        val token3 = WildlifeToken(Animal.FOX)
        val token4 = WildlifeToken(Animal.BEAR)

        game.shop.addAll(
            listOf(
                Pair(tile1, token1),
                Pair(tile2, token2),
                Pair(tile3, token3),
                Pair(tile4, token4)
            )
        )
        val sameAnimalResult = gameService.checkForSameAnimal(listOf(0, 1, 2))
        assertTrue(sameAnimalResult, "Die Tokens an den Indizes 0, 1 und 2 sollten alle 'FOXY' sein.")

        val differentAnimalResult = gameService.checkForSameAnimal(listOf(0, 1, 3))
        assertFalse(differentAnimalResult, "Die Tokens an den Indizes 0, 1 und 3 sollten nicht alle 'FOXY' sein.")
        assertThrows<IllegalArgumentException> {
            gameService.checkForSameAnimal(listOf(0, 1, 4))
        }

        assertThrows<IllegalArgumentException> {
            gameService.checkForSameAnimal(listOf(0, 0, 1))
        }

        assertThrows<IllegalArgumentException> {
            gameService.checkForSameAnimal(listOf())
        }
        assertThrows<IllegalArgumentException> {
            gameService.checkForSameAnimal(listOf(0, 1, 2, 3, 4))
        }
    }
    @Test
    fun testExecuteTokenReplacement() {
        val rootService = RootService()
        val gameService = GameService(rootService)

        gameService.startNewGame(
            mapOf("Alice" to PlayerType.LOCAL,
                "Bob" to PlayerType.EASY),
            listOf(true, false, true, false, true),
            false,
            false,
            null
        )

        val game = rootService.currentGame
        checkNotNull(game)

        game.shop.clear()
        val tile1 = HabitatTile(1, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.FOREST))
        val tile2 = HabitatTile(2, false, 0, listOf(Animal.FOX), null, mutableListOf(Terrain.RIVER))
        val tile3 = HabitatTile(3, false, 0, listOf(Animal.BEAR), null, mutableListOf(Terrain.PRAIRIE))
        val tile4 = HabitatTile(4, false, 0, listOf(Animal.HAWK), null, mutableListOf(Terrain.PRAIRIE))

        val token1 = WildlifeToken(Animal.FOX)
        val token2 = WildlifeToken(Animal.FOX)
        val token3 = WildlifeToken(Animal.ELK)
        val token4 = WildlifeToken(Animal.ELK)

        game.shop.addAll(
            listOf(
                Pair(tile1, token1),
                Pair(tile2, token2),
                Pair(tile3, token3),
                Pair(tile4, token4)
            )
        )

        game.wildlifeTokenList.clear()
        game.wildlifeTokenList.addAll(
            listOf(
                WildlifeToken(Animal.HAWK),
                WildlifeToken(Animal.HAWK),
                WildlifeToken(Animal.SALMON),
                WildlifeToken(Animal.BEAR)
            )
        )

        val originalShop = game.shop.map { it.copy() }

        val indicesToReplace = listOf(0, 2)

        gameService.executeTokenReplacement(indicesToReplace)

        val replacedTokens = indicesToReplace.map { game.shop[it].second }
        assertTrue(
            replacedTokens.none { token -> originalShop[indicesToReplace[0]].second == token }
        )

        assertEquals(4, game.wildlifeTokenList.size)
    }


}