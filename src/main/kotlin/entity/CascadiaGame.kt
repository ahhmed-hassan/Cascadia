package entity

class CascadiaGame(
    val startTileList: List<HabitatTile>, //muss ListHabitatTile sein?
    val ruleSet: List<Boolean>, // False is A, true is B
    val simulationSpeed: Float,
    var natureToken: Int,
    var hasReplacedThreeToken: Boolean,
    var hasPlayedTile: Boolean,
    var shop: List<Pair<HabitatTile,WildlifeToken>>,
    private var discardedToken: List<WildlifeToken>,
    /****/
    var currentPlayer: Player?,
    val playerList: Player,
    val habitatTileList: HabitatTile,
    var selectedTile: HabitatTile,
    val wildlifeToken: WildlifeToken?
    ) {

}