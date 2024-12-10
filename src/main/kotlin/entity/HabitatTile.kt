package entity

/**
 * [HabitatTile] Represents a habitat tile in the Cascadia game.
 * Habitat tiles form the playing area and may have associated wildlife symbols and tokens.
 *
 * @property id Unique identifier for the habitat tile.
 * @property isKeystoneTile Indicates if the tile is a keystone tile (special tile in the game).
 * @property rotationOffset Rotation offset for the tile, represented as an integer between 0 and 5.
 *
 * @property wildlifeSymbols The type of wildlife symbol associated with this tile (e.g., Bear, Elk).
 * @property wildlifeToken The wildlife token currently placed on this tile. Nullable as not all tiles may have tokens.
 * @property terrains The terrain type(s) represented on the tile.
 */
class HabitatTile(
    val id: Int,
    var isKeystoneTile: Boolean,
    var rotationOffset: Int,
    /****/
    val wildlifeSymbols: Animal,
    var wildlifeToken: WildlifeToken,
    val terrains: Terrain
) {
}