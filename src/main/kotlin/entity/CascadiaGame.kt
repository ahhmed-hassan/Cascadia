package entity

/**
 * [CascadiaGame] Represents a Cascadia Game.
 *
 * @property startTileList List of List starting habitat tiles for the game.
 * @property ruleSet Defines the rule set for the game. False represents Rule A, and True represents Rule B.
 * @property simulationSpeed Speed at which the game simulation runs for Bots, represented as a floating-point value.
 * @property natureToken The number of nature tokens available in the game for each Player.
 * @property hasReplacedThreeToken Indicates whether the player has replaced three tokens during this turn.
 * @property hasPlayedTile Indicates whether the player has placed a tile during this turn.
 * @property shop List of pairs containing a habitat tile and a wildlife token currently available in the shop.
 *
 * @property discardedToken List of wildlife tokens that have been discarded during the game.
 * @property currentPlayer The player currently taking their turn.
 * @property playerList List of all players participating in the game.
 * @property habitatTileList List of habitat tiles available in the game.
 * @property selectedTile The habitat tile currently selected by the player.
 * @property wildlifeToken The wildlife token currently associated with the selected tile or action.
 */
class CascadiaGame(
    val startTileList: List<List<HabitatTile>>,
    val ruleSet: List<Boolean>,
    val simulationSpeed: Float,
    var natureToken: Int,
    var hasReplacedThreeToken: Boolean,
    var hasPlayedTile: Boolean,
    var shop: List<Pair<HabitatTile,WildlifeToken>>,
    private var discardedToken: List<WildlifeToken>,
    /****/
    var currentPlayer: Player,
    val playerList: List<Player>,
    val habitatTileList: List<HabitatTile>,
    var selectedTile: HabitatTile,
    var wildlifeToken: WildlifeToken
    ) {

}