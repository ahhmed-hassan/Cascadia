package service

import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

/**
 * Enumeration representing the various states encountered in networked games, particularly during
 * the connection and game setup phases. This enum is utilized within the [NetworkService] class.
 */
enum class ConnectionState {
    /**
     * Represents the state where no connection is active. This is the initial state when the program
     * starts or after an active connection has been terminated.
     */
    DISCONNECTED,
    /**
     * Represents the state where the client is connected to the server, but no game has been
     * started or joined yet.
     */
    CONNECTED,
    /**
     * Represents the state where a hostGame request has been sent to the server, and the system
     * is waiting for a confirmation response (i.e., [CreateGameResponse]).
     */
    HOST_WAITING_FOR_CONFIRMATION,
    /**
     * Represents the state where a joinGame request has been sent to the server, and the system
     * is waiting for a confirmation response (i.e., [JoinGameResponse]).
     */
    GUEST_WAITING_FOR_CONFIRMATION,
    /**
     * Represents the state where the host game has started, and the system is waiting for the
     * guest player to join.
     */
    WAITING_FOR_GUESTS,
    /**
     * Represents the state where the player has joined the game as a guest and is waiting for
     * the host to send the initialization message (i.e., [GameInitMessage]).
     */
    WAITING_FOR_INIT,
    /**
     * Represents the state where the game is running, and it is the player's turn.
     */
    PLAYING_MY_TURN,
    /**
     * Represents the state where the player has an Overpopulation to resolve
     */
    SWAPPING_WILDLIFE_TOKENS,
    /**
     * Represents the State where the opponent hast an Overpopulation th resolve
     */
    OPPONENT_SWAPPING_WILDLIFE_TOKENS,
    /**
     * Represents the state where the game is running, the player has completed their turn, and is
     * now waiting for the opponent to submit their turn.
     */
    WAITING_FOR_OPPONENTS_TURN,

    /**
     * Represents the State where the game ends
     */
    GAME_ENDED,
}