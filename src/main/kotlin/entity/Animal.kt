package entity

/**
 * Enum [Animal] representing the types of animals found in the Cascadia game.
 * Each animal has specific behaviors and scoring rules defined in the game.
 *
 * BEAR, Represents a bear in the game.
 * ELK, Represents an elk in the game.
 * FOX, Represents a fox in the game.
 * HAWK, Represents a hawk in the game.
 * SALMON, Represents a salmon in the game.
 */
enum class Animal(val shortCut : Char) {
    BEAR('B'),
    ELK('E'),
    FOX('F'),
    HAWK('H'),
    SALMON('S'),
    ;

    companion object{
        fun fromShortCut (shortCut: Char) : Animal {
            return Animal.values().find { it.shortCut == shortCut }
                ?:throw IllegalArgumentException("Invalid Terrain: $shortCut")
        }
    }

    override fun toString(): String = when (this) {
        BEAR -> "B"
        ELK -> "E"
        FOX -> "F"
        HAWK -> "H"
        SALMON -> "S"
    }
}