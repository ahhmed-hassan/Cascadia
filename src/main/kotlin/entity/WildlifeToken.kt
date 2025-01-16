package entity

/**
 * [WildlifeToken] Represents a wildlife token in the Cascadia game.
 * Wildlife tokens are associated with specific animals and can be placed on habitat tiles.
 *
 * @property animal The animal type represented by this token (e.g., Bear, Elk).
 */
data class WildlifeToken(
    val animal: Animal
)