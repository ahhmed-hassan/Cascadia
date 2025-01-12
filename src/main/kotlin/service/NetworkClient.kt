package service

import entity.PlayerType
import edu.udo.cs.sopra.ntf.messages.*
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.*

/**
 * Represents the [BoardGameClient] implementation for network communication.
 *
 * @param playerName The name of the client.
 * @param playerType The type of the player.
 * @param networkService The [NetworkService] to potentially forward received messages to.
 * @param secret A secret key used for secure communication.
 */

class NetworkClient (playerName: String, host: String, secret: String, val networkService: NetworkService,
                     val playerType: PlayerType): BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE)  {

    /** the identifier of this game session; can be null if no session started yet. */
    var sessionID: String? = null

    /**
     * Represents the method that handles a [CreateGameResponse] received from the server.
     * It waits for the guest player when the response status is [CreateGameResponseStatus.SUCCESS],
     * in order to manage potential network issues.
     *
     * @throws IllegalStateException if the status is not success, or if the system is not currently awaiting
     * a game creation response.
     */
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.HOST_WAITING_FOR_CONFIRMATION)
            { "unexpected CreateGameResponse" }

            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_GUESTS)
                    sessionID = response.sessionID
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [JoinGameResponse] sent by the server. Will await the init message when its
     * status is [JoinGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * @throws IllegalStateException if status != success or currently not waiting for a join game response.
     */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.GUEST_WAITING_FOR_CONFIRMATION)
            {"unexpected JoinGameResponse"}

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    sessionID = response.sessionID
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_INIT)
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handles a [PlayerJoinedNotification] sent by the server. This method verifies that player names
     * are unique, adds new players to the player list, and starts the game once the maximum number
     * of players has been reached.
     *
     * @throws IllegalStateException if not currently expecting any guests to join.
     * @throws IllegalStateException if the maximum number of players has already been reached.
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_GUESTS) {
                "not awaiting any guests."
            }

            val players = networkService.playersList
            val maxPlayers = 4

            // Überprüfen, ob Spielername bereits existiert
            if (players.contains(notification.sender)) {
                disconnectAndError("Player names are not unique!")
            }

            // Spieler hinzufügen, sofern max. Kapazität noch nicht erreicht
            if (players.size < maxPlayers) {
                players.add(notification.sender)
                networkService.refreshPlayerList(players)

            } else {
                disconnectAndError("Maximum number of players has been reached.")
            }
        }
    }

    /**
     * Handle a [GameInitMessage] sent by the server.
     * @throws IllegalStateException when the player is not waiting for [GameInitMessage]
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onInitReceived(message: GameInitMessage, sender: String) {
        check(networkService.connectionState == ConnectionState.WAITING_FOR_INIT)
        {"Not waiting for initMessage"}

        BoardGameApplication.runOnGUIThread {
            networkService.startNewJoinedGame(
                message = message
            )
        }
    }

    /**
     * Handles a [ResolveOverpopulationMessage] sent by the server. This message indicates that
     * an overpopulation situation (of three or four) has been resolved. The network state will
     * be updated accordingly, and the game will continue to the next phase or turn.
     *
     * @throws IllegalStateException when the player is not in the correct state to resolve overpopulation.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onResolveOverPopulation(message: ResolveOverpopulationMessage, sender: String) {
        check(networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS_TURN ||
        networkService.connectionState == ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS)
        { "Not Opponent's turn" }

        BoardGameApplication.runOnGUIThread {
            networkService.resolvedOverPopulationMessage(message, sender)
        }
    }

    /**
     * Handles a [PlaceMessage] sent by the server. This message communicates the placement of a tile
     * and associated wildlife tokens on the board. The coordinates, tile rotation, and any use of a
     * nature token are processed to update the game state accordingly.
     *
     * @throws IllegalStateException when the player is not in the correct state to place a tile.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onPlacedMessage(message: PlaceMessage, sender: String) {
        check(networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        BoardGameApplication.runOnGUIThread {
            networkService.placedMessage(message, sender)
        }
    }

    /**
    * Handles a [ShuffleWildlifeTokensMessage] sent by the server. This message indicates that
    * the wildlife tokens have been shuffled at the end of the overpopulation phase.
    *
    * @throws IllegalStateException when the player is not in the correct state for receiving shuffled tokens.
    */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onShuffledWildlifeTokens(message: ShuffleWildlifeTokensMessage, sender: String) {
        check(networkService.connectionState == ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS)
        { "Not waiting for shuffleWilfelifeToken" }
        BoardGameApplication.runOnGUIThread {
            networkService.shuffledWildlifeTokensMessage(message, sender)
        }
    }

    /**
     * Handles a [SwappedWithNatureTokenMessage] sent by the server. This message indicates that
     * a swap of wildlife tokens has occurred using a nature token.
     *
     * @throws IllegalStateException when the player is not in the correct state to process a swap with nature tokens.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onSwappedWithNatureTokenMessage(message: SwappedWithNatureTokenMessage, sender: String) {
        check(networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        { "Not waiting for shuffleWilfelifeToken" }
        BoardGameApplication.runOnGUIThread {
            networkService.swappedWithNatureTokenMessage(message, sender)
        }
    }

    /**
     * Represents the method to disconnect the client and handle an error.
     *
     * @param message The error message to be processed.
     * @throws IllegalStateException with the provided error message.
     */
    fun disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }


}