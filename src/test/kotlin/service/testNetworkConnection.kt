package service

import entity.PlayerType
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNotNull

class testNetworkConnection {
    private lateinit var rootServiceHost: RootService
    private lateinit var rootServiceGuest: RootService

    companion object {
        const val NETWORK_SECRET = "cascadia24d"
    }

    private fun initConnections() {
        rootServiceHost = RootService()
        rootServiceGuest = RootService()

        rootServiceHost.networkService.hostGame(NETWORK_SECRET, generateRandomNumberAsString(), "Rodi", PlayerType.LOCAL)

        assert(rootServiceHost.waitForState(ConnectionState.WAITING_FOR_GUESTS)) {
            error("Nach dem Warten nicht im Zustand angekommen")
        }
        val hostClient = rootServiceHost.networkService.client
        assertNotNull(hostClient)
        rootServiceGuest.networkService.joinGame(NETWORK_SECRET, "Mehi", hostClient.sessionID!!, PlayerType.NETWORK)

        assert(rootServiceGuest.waitForState(ConnectionState.WAITING_FOR_INIT)){
            error("Nach dem Warten nicht im Zustand angekommen")
        }

    }
    @Test
    fun testHostAndJoinGame() {


        initConnections()
    }
    private fun RootService.waitForState(state: ConnectionState, timeout: Int = 5000):Boolean {
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

    fun generateRandomNumberAsString(): String {
        // Define the range for the random number.
        val lowerBound = 2001
        val upperBound = Int.MAX_VALUE

        // Generate a random number within the defined range and convert it to a String.
        return Random.nextInt(lowerBound, upperBound).toString()
    }
}