package entity

/**
 * Enum [PlayerType] representing the different types of players in the Cascadia game.
 * This classification determines the player's mode of interaction with the game.
 *
 * @property LOCAL Represents a local player controlling the game directly on the device.
 * @property EASY Represents an AI player with an easy difficulty level.
 * @property NORMAL Represents an AI player with a normal difficulty level.
 * @property NETWORK Represents a player connected through a network for multiplayer gameplay.
 */
enum class PlayerType {
    LOCAL,
    EASY,
    NORMAL,
    NETWORK
}