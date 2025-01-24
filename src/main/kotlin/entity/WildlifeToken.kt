package entity

/**
 * [WildlifeToken] Represents a wildlife token in the Cascadia game.
 * Wildlife tokens are associated with specific animals and can be placed on habitat tiles.
 *
 * @property animal The animal type represented by this token (e.g., Bear, Elk).
 */
class WildlifeToken(
    val animal: Animal
)

/**
 * this function clones Wildlife Tokens
 */
fun WildlifeToken.clone() : WildlifeToken {
    val animal = WildlifeToken(animal)
    return animal
}