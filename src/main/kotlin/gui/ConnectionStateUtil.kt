package gui

import service.ConnectionState

/**
 * provides a corresponding text for this [ConnectionState] to be displayed in the GUI.
 */
fun ConnectionState.toUIText() =
    when(this) {
        ConnectionState.DISCONNECTED -> "Disconnected."
        ConnectionState.CONNECTED -> "Connected."
        ConnectionState.HOST_WAITING_FOR_CONFIRMATION -> "Waiting for server to create game."
        ConnectionState.GUEST_WAITING_FOR_CONFIRMATION -> "Waiting for server to join game."
        ConnectionState.WAITING_FOR_GUESTS -> "Waiting for guest player."
        ConnectionState.WAITING_FOR_INIT -> "Waiting for host player to start game"
        ConnectionState.PLAYING_MY_TURN -> "It's my turn"
        ConnectionState.SWAPPING_WILDLIFE_TOKENS -> "The player has an overpopulation to resolve"
        ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS -> "Opponent has an overpopulation to resolve"
        ConnectionState.WAITING_FOR_OPPONENTS_TURN -> "Waiting for opponent's turn"
        ConnectionState.GAME_ENDED -> "Game Ended"
    }