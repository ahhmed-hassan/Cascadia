package entity

/**
 * Represents a player in Cascadia with the following attributes:
 *
 * @param name The name of the player.
 * @param habitat A map representing the player's placed habitat tiles and wildlife tokens.
 * @param playerType The type of the player, which signalises if the player is human or a bot.
 *
 * @property hasPlayedTile Indicates whether the player has already played a tile in the current turn.
 * @property natureToken The number of nature tokens the player currently holds.
 * @property score The player's current score.
 */
class Player(name: String, habitat: Map<Pair<Int, Int>, HabitatTile>, playerType: PlayerType) {
    var hasPlayedTile : Boolean = false
    var natureToken : Int = 0
    var score : Int = 0
}