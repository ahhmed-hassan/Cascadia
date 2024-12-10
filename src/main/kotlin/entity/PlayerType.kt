package entity

/**
 * Enum [PlayerType] representing the different types of players in the Cascadia game.
 * This classification determines the player's mode of interaction with the game.
 *
 * LOCAL, Represents a local player controlling the game directly on the device.
 *  EASY, Represents an AI player with an easy difficulty level.
 *  NORMAL, Represents an AI player with a normal difficulty level.
 * NETWORK, Represents a player connected through a network for multiplayer gameplay.
 */
enum class PlayerType {
    LOCAL,
    EASY,
    NORMAL,
    NETWORK,
    ;
}