package entity

class HabitatTile(
    val id: Int,
    var isKeystoneTile: Boolean,
    /****/
    val wildlifeSymbols: Animal,
    var wildlifeToken: WildlifeToken?,
    val terrains: Terrain
) {
}