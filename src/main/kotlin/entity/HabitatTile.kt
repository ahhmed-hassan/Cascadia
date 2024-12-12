package entity

/**
 * [HabitatTile] Represents a habitat tile in the Cascadia game.
 * Habitat tiles form the playing area and may have associated wildlife symbols and tokens.
 *
 * @property id Unique identifier for the habitat tile.
 * @property isKeystoneTile Indicates if the tile is a keystone tile (special tile in the game).
 * @property rotationOffset Rotation offset for the tile, represented as an integer between 0 and 5.
 *
 * @property wildlifeSymbols List of the type of wildlife symbols associated with this tile (e.g., Bear, Elk).
 * @property wildlifeToken The wildlife token currently placed on this tile. Nullable as not all tiles may have tokens.
 * @property terrains Mutable list of the terrain type(s) represented on the tile.
 */
class HabitatTile(
    val id: Int,
    val isKeystoneTile: Boolean,
    var rotationOffset: Int,

    val wildlifeSymbols: List<Animal>,
    var wildlifeToken: WildlifeToken?,
    val terrains: MutableList<Terrain>
) {
}