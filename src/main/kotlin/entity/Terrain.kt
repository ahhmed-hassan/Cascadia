package entity

/**
 * Enum [Terrain] representing the different terrain types in the Cascadia game.
 * Each terrain type is associated with specific habitat tiles.
 *
 * FOREST, Represents a forest terrain.
 * MOUNTAIN, Represents a mountain terrain.
 * PRAIRIE, Represents a prairie terrain.
 * RIVER, Represents a river terrain.
 * WETLAND, Represents a wetland terrain.
 */
enum class Terrain(val shortCut : Char) {
    FOREST('F'),
    MOUNTAIN('M'),
    PRAIRIE('P'),
    RIVER('R'),
    WETLAND('W'),
    ;

    companion object{
        fun fromShortCut (shortCut: Char) : Terrain {
            return values().find { it.shortCut == shortCut }
                ?:throw IllegalArgumentException("Invalid Terrain: $shortCut")
        }
    }

    override fun toString(): String = when (this) {
        FOREST -> "F"
        MOUNTAIN -> "M"
        PRAIRIE -> "P"
        RIVER -> "R"
        WETLAND -> "W"
    }

}