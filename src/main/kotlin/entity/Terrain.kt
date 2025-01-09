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
enum class Terrain {
    FOREST,
    MOUNTAIN,
    PRAIRIE,
    RIVER,
    WETLAND,
    ;

    companion object {

        /**
         * Transfrom a given first letter into its responding Terrain
         *
         * @param value the first letter of a member of this enum
         */
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