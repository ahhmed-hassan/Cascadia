package entity

import entity.CardSuit.*

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
enum class Terrain {
    FOREST,
    MOUNTAIN,
    PRAIRIE,
    RIVER,
    WETLAND,
    ;

    override fun toString() = when(this) {
        FOREST -> "F"
        MOUNTAIN -> "M"
        PRAIRIE -> "P"
        RIVER -> "R"
        WETLAND -> "W"
    }

    companion object {
        fun fromValue(value : String) : Terrain {
            return when(value) {
                "F" ->  FOREST
                "M" ->  MOUNTAIN
                "P" ->  PRAIRIE
                "R" ->  RIVER
                else ->  WETLAND
            }
        }
    }





}