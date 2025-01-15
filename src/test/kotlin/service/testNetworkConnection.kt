package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 *  Test class for testing the connection to the network.
 */
class testNetworkConnection {
    private lateinit var rootServiceHost: RootService
    private lateinit var rootServiceGuest: RootService

    companion object {
        const val NETWORK_SECRET = "cascadia24d"
    }

    /**
     * Initialize Connection for Network Game and test the connection with the server
     */
    private fun initConnections() {
        rootServiceHost = RootService()
        rootServiceGuest = RootService()

        rootServiceHost.networkService.hostGame(NETWORK_SECRET, null, "Rodi", PlayerType.NETWORK)

        assert(rootServiceHost.waitForState(ConnectionState.WAITING_FOR_GUESTS)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }
        val hostClient = rootServiceHost.networkService.client
        assertNotNull(hostClient)
        rootServiceGuest.networkService.joinGame(NETWORK_SECRET, "Mehi", hostClient.sessionID!!, PlayerType.NETWORK)

        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_INIT)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }

    }

    /**
     *  Test for hosting and joining Games via network
     */
    @Test
    fun testHostAndJoinGame() {
        initConnections()
        assertEquals(2, rootServiceHost.networkService.playersList.size)
        val scoreRules = listOf(false, false, true, false, false)
        rootServiceHost.networkService.startNewHostedGame(orderIsRanom = false, isRandomRules = false,
            scoreRules = scoreRules
        )

        assertEquals(ConnectionState.PLAYING_MY_TURN, rootServiceHost.networkService.connectionState)
        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)) {
            error("Nach dem Warten nicht im Zustand angekommen") }

        val hostGame = rootServiceHost.currentGame
        val guestGame = rootServiceGuest.currentGame
        assertNotNull(hostGame)
        assertNotNull(guestGame)

        // Hole den aktuellen Spieler von Host und Guest
        val hostCurrentPlayer = hostGame.currentPlayer
        val guestCurrentPlayer = guestGame.currentPlayer
        assertNotNull(hostCurrentPlayer)
        assertNotNull(guestCurrentPlayer)

        // Prüfe die relevanten Habitat-Koordinaten
        val habitatPositions = listOf(
            0 to 0, // zentrale Kachel
            1 to -1, // rechts unten
            1 to 0  // links unten
        )

        assertEquals(hostGame.startTileList.size, guestGame.startTileList.size)
        assertEquals(hostCurrentPlayer.name, guestCurrentPlayer.name)

        for (position in habitatPositions) {
            val hostTile = hostCurrentPlayer.habitat[position]
            val guestTile = guestCurrentPlayer.habitat[position]

            // Sicherstellen, dass beide Kacheln existieren und gleich sind
            assertNotNull(hostTile) { "Host has no tile at $position" }
            assertNotNull(guestTile) { "Guest has no tile at $position" }
            assertEquals(hostTile.id, guestTile.id)
        }
        // Überprüfe die `shop`-Listen
        val hostShop = hostGame.shop
        val guestShop = guestGame.shop

        assertEquals(hostShop.size, guestShop.size, "Shop sizes do not match")
        for (i in hostShop.indices) {
            val hostPair = hostShop[i]
            val guestPair = guestShop[i]

            // Vergleiche HabitatTile und WildlifeToken
            assertEquals(hostPair.first?.id, guestPair.first?.id, "Mismatch in HabitatTile at index $i")
            assertEquals(hostPair.second?.animal, guestPair.second?.animal,
                "Mismatch in WildlifeToken at index $i"
            )
        }

        val hostHabitatTileList = hostGame.habitatTileList
        val guestHabitatTileList = guestGame.habitatTileList

        val hostWildLifeTokens = hostGame.wildlifeTokenList
        val guestWildLifeTokens = guestGame.wildlifeTokenList

        for (i in guestHabitatTileList.indices) {
            val guestHabitatTile = guestHabitatTileList[i].id
            val hostHabitatTile = hostHabitatTileList[i].id
            assertEquals(hostHabitatTile, guestHabitatTile)
        }

        for (i in guestWildLifeTokens.indices) {
            val guestWildLifeToken = guestWildLifeTokens[i].animal
            val hostWildLifeToken = hostWildLifeTokens[i].animal
            assertEquals(hostWildLifeToken, guestWildLifeToken)
        }

    }

    /**
     * Test sending and receiving resolveOverPopulation Message
     */
    @Test
    fun testResolveOverPopulation() {
        initConnections()
        assertEquals(2, rootServiceHost.networkService.playersList.size)
        val scoreRules = listOf(false, false, true, false, false)

        rootServiceHost.networkService.startNewHostedGame(orderIsRanom = false, isRandomRules = false,
            scoreRules = scoreRules
        )

        assertEquals(ConnectionState.PLAYING_MY_TURN, rootServiceHost.networkService.connectionState)
        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }

        val hostGame = rootServiceHost.currentGame
        val guestGame = rootServiceGuest.currentGame
        assertNotNull(hostGame)
        assertNotNull(guestGame)

        val hostCurrentPlayer = hostGame.currentPlayer
        val guestCurrentPlayer = guestGame.currentPlayer
        assertNotNull(hostCurrentPlayer)
        assertNotNull(guestCurrentPlayer)

        val tokenTileList = createTileTokenPairs(true)
        hostGame.shop.clear()
        tokenTileList.forEach { pair -> hostGame.shop.add(pair) }
        guestGame.shop.clear()
        tokenTileList.forEach { pair -> guestGame.shop.add(pair) }

        rootServiceHost.gameService.resolveOverpopulation()
        assertEquals(ConnectionState.SWAPPING_WILDLIFE_TOKENS, rootServiceHost.networkService.connectionState)
        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }

        val hostHabitatTileList = hostGame.habitatTileList
        val guestHabitatTileList = guestGame.habitatTileList
        val hostWildLifeTokens = hostGame.wildlifeTokenList
        val guestWildLifeTokens = guestGame.wildlifeTokenList

        assertEquals(guestHabitatTileList.size, hostHabitatTileList.size)

        for (i in guestHabitatTileList.indices) { val guestHabitatTile = guestHabitatTileList[i].id
            val hostHabitatTile = hostHabitatTileList[i].id
            assertEquals(hostHabitatTile, guestHabitatTile)
        }

        assertEquals(guestWildLifeTokens.size, hostWildLifeTokens.size)
        Thread.sleep(300)
        val guestGame2 = rootServiceGuest.currentGame
        assertNotNull(guestGame2)

        for (i in guestWildLifeTokens.indices) { val guestWildLifeToken = guestGame2.wildlifeTokenList[i].animal
            val hostWildLifeToken = hostWildLifeTokens[i].animal
            assertEquals(hostWildLifeToken, guestWildLifeToken)
        }

        val hostShop = hostGame.shop
        val guestShop = guestGame2.shop
        assertEquals(hostShop.size, guestShop.size, "Shop sizes do not match")
        for (i in hostShop.indices) { val hostPair = hostShop[i]
            val guestPair = guestShop[i]
            assertEquals(hostPair.first?.id, guestPair.first?.id, "Mismatch in HabitatTile at index $i")
            assertEquals(hostPair.second?.animal, guestPair.second?.animal,
                "Mismatch in WildlifeToken at index $i"
            )
        }
    }

    /**
     * Test sending and reveiving SwappedWithNatureToken message
     */
    @Test
    fun testSwappedWithNatureToken() {
        initConnections()
        assertEquals(2, rootServiceHost.networkService.playersList.size)
        val scoreRulse = listOf(false, false, true, false, false)

        rootServiceHost.networkService.startNewHostedGame(
            orderIsRanom = false, isRandomRules = false, scoreRules = scoreRulse
        )

        assertEquals(ConnectionState.PLAYING_MY_TURN, rootServiceHost.networkService.connectionState)
        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }

        val hostGame = rootServiceHost.currentGame
        val guestGame = rootServiceGuest.currentGame
        assertNotNull(hostGame)
        assertNotNull(guestGame)

        val hostCurrentPlayer = hostGame.currentPlayer
        val guestCurrentPlayer = guestGame.currentPlayer
        assertNotNull(hostCurrentPlayer)
        assertNotNull(guestCurrentPlayer)

        hostCurrentPlayer.natureToken++
        guestCurrentPlayer.natureToken++

        println(hostCurrentPlayer.natureToken)

        val tokenTileList = createTileTokenPairs()
        hostGame.shop.clear()
        tokenTileList.forEach { pair -> hostGame.shop.add(pair) }
        guestGame.shop.clear()
        tokenTileList.forEach { pair -> guestGame.shop.add(pair) }

        val hostWildLifeTokens = hostGame.wildlifeTokenList
        val guestWildLifeTokens = guestGame.wildlifeTokenList

        for (i in guestWildLifeTokens.indices) {
            val guestWildLifeToken = guestGame.wildlifeTokenList[i].animal
            val hostWildLifeToken = hostWildLifeTokens[i].animal
            assertEquals(hostWildLifeToken, guestWildLifeToken)
        }

        rootServiceHost.playerActionService.replaceWildlifeTokens(listOf(1, 2))

        assertEquals(0, hostGame.currentPlayer.natureToken)
        Thread.sleep(300)
        val guestGame2 = rootServiceGuest.currentGame
        assertNotNull(guestGame2)

        for (i in guestWildLifeTokens.indices) {
            val guestWildLifeToken = guestGame2.wildlifeTokenList[i].animal
            val hostWildLifeToken = hostWildLifeTokens[i].animal
            assertEquals(hostWildLifeToken, guestWildLifeToken)
        }

        val hostShop = hostGame.shop
        val guestShop = guestGame2.shop
        assertEquals(hostShop.size, guestShop.size, "Shop sizes do not match")
        for (i in hostShop.indices) {
            val hostPair = hostShop[i]
            val guestPair = guestShop[i]

            assertEquals(hostPair.first?.id, guestPair.first?.id, "Mismatch in HabitatTile at index $i")

            assertEquals(
                hostPair.second?.animal, guestPair.second?.animal, "Mismatch in WildlifeToken at index $i"
            )
        }
    }

    /**
     * Helper function to create token-tile pairs for testing
     *
     * @param overpop is a flag to create an overpopulation of four in pairs
     *
     */
    private fun createTileTokenPairs(overpop: Boolean = false): List<Pair<HabitatTile, WildlifeToken>> {
        val tile1 = HabitatTile(
            1, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.FOREST)
        )
        val tile2 = HabitatTile(
            2, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.RIVER)
        )
        val tile3 = HabitatTile(
            3, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.PRAIRIE)
        )
        val tile4 = HabitatTile(
            4, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.PRAIRIE)
        )
        if (overpop) {
            val token1 = WildlifeToken(Animal.FOX)
            val token2 = WildlifeToken(Animal.FOX)
            val token3 = WildlifeToken(Animal.FOX)
            val token4 = WildlifeToken(Animal.FOX)

            return listOf(Pair(tile1, token1), Pair(tile2, token2), Pair(tile3, token3), Pair(tile4, token4))
        } else {
            val token1 = WildlifeToken(Animal.HAWK)
            val token2 = WildlifeToken(Animal.FOX)
            val token3 = WildlifeToken(Animal.BEAR)
            val token4 = WildlifeToken(Animal.ELK)

            return listOf(Pair(tile1, token1), Pair(tile2, token2), Pair(tile3, token3), Pair(tile4, token4))
        }
    }

    /**
     * test sending and receiving Place Message
     */
    @Test
    fun testPlaceMessage() {
        initConnections()
        assertEquals(2, rootServiceHost.networkService.playersList.size)
        val scoreRulse = listOf(false, false, true, false, false)

        rootServiceHost.networkService.startNewHostedGame(
            orderIsRanom = false,
            isRandomRules = false,
            scoreRules = scoreRulse
        )

        assertEquals(ConnectionState.PLAYING_MY_TURN, rootServiceHost.networkService.connectionState)
        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }

        val hostGame = rootServiceHost.currentGame
        val guestGame = rootServiceGuest.currentGame
        assertNotNull(hostGame)
        assertNotNull(guestGame)

        val hostCurrentPlayer = hostGame.currentPlayer
        val guestCurrentPlayer = guestGame.currentPlayer
        val guest = guestGame.playerList.indexOf(guestCurrentPlayer)

        assertNotNull(hostCurrentPlayer)
        assertNotNull(guestCurrentPlayer)

        hostCurrentPlayer.natureToken++
        guestCurrentPlayer.natureToken++

        println(hostCurrentPlayer.natureToken)

        val tokenTileList = createTileTokenPairs().toMutableList()
        hostGame.shop.clear()

        tokenTileList.forEach { pair -> hostGame.shop.add(pair) }
        guestGame.shop.clear()
        tokenTileList.forEach { pair -> guestGame.shop.add(pair) }

        rootServiceHost.playerActionService.chooseTokenTilePair(1)
        assertEquals(rootServiceHost.networkService.placedTileIndex, 1)
        assertEquals(rootServiceHost.networkService.selectedTokenIndex, 1)

        // rootServiceHost.playerActionService.chooseCustomPair(1,2)
        // assertEquals(rootServiceHost.networkService.placedTileIndex, 1)
        // assertEquals(rootServiceHost.networkService.selectedTokenIndex, 2)
        // assertTrue(rootServiceHost.networkService.usedNatureToken)
        val tile2 = HabitatTile(
            2, false, 0, listOf(Animal.FOX), null,
            mutableListOf(Terrain.RIVER)
        )
        rootServiceHost.playerActionService.addTileToHabitat(0 to -1)
        assertEquals(rootServiceHost.networkService.tileCoordinates, 0 to -1)
        rootServiceHost.playerActionService.addToken(tile2)

        assertEquals(tile2.wildlifeToken?.animal, Animal.FOX)

        assertNull(rootServiceHost.networkService.placedTileIndex)
        assertNull(rootServiceHost.networkService.tokenCoordinates)

        Thread.sleep(300)

        assertEquals(guestGame.playerList[guest].habitat.get(0 to -1)?.id, 2)

        rootServiceHost.networkService.disconnect()
        rootServiceGuest.networkService.disconnect()

    }

    private fun RootService.waitForState(state: ConnectionState, timeout: Int = 5000): Boolean {
        var timePassed = 0
        while (timePassed < timeout) {
            if (networkService.connectionState == state)
                return true
            else {
                Thread.sleep(100)
                timePassed += 100
            }
        }
        return false
    }

}