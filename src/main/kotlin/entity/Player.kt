package entity

/**
 * Represents a player in Cascadia with the following attributes:
 *
 * @param name The name of the player.
 * @param habitat A map representing the player's placed habitat tiles and wildlife tokens.
 * @param playerType The type of the player, which signalises if the player is human or a bot.
 *
 * @property natureToken The number of nature tokens the player currently holds.
 * @property score The player's current score.
 */
class Player(val name: String, val habitat: MutableMap<Pair<Int, Int>, HabitatTile>, val playerType: PlayerType) {
    var natureToken : Int = 0
    var score : Int = 0
}
