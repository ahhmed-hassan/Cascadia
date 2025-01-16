package entity

class HardBotPossiblePlacements(
    val tileId: Int,
    val tilePlacement: Pair<Int, Int>,
    val rotation: Int,
    val wildlifeToken: WildlifeToken? = null,
    val wildlifePlacementId: Int? = null,
    val customPair: Pair<Int, Int>? = null,
    val replacedWildlife: Boolean,
    val wildLifeChance: Double? = null,
    val usedNaturalToken: Int
) {
    var score = 0
    var numberOfScores = 0

    fun getScore(): Double {
        return score.toDouble() / numberOfScores.toDouble()
    }
}