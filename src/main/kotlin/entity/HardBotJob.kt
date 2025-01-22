package entity

/**
 *
 */
class HardBotJob(
    val habitat: MutableMap<Pair<Int, Int>, HabitatTile>,
    var round: Int,
    val habitatTiles: MutableList<HabitatTile>,
    val animals: MutableList<WildlifeToken>,
    val employer: HardBotPossiblePlacements,
    val shop: MutableList<Pair<HabitatTile?, WildlifeToken?>>
)
