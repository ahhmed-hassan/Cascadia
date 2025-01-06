package entity

class HardBotPossiblePlacements(
    val tile: HabitatTile,
    val tilePlacement: Pair<Int, Int>,
    val rotation: Int,
    val wildlifeToken: WildlifeToken? = null,
    val wildlifePlacement: HabitatTile? = null,
    val customPair: Pair<Int, Int>? = null,
    val replacedWildlife: BooleanArray? = null,
    val wildLifeChance: Number? = null,
    val usedNaturalToken: Boolean
) {
    var score = 0
    var numberOfScores = 0

    fun getScore(): Number {
        return score / numberOfScores
    }
}