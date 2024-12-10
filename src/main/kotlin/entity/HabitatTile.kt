package entity

class HabitatTile(
    val id: Int,
    var isKeystoneTile: Boolean,
    var rotationOffset: Int, //Integer between 0 and 5
    /****/
    val wildlifeSymbols: Animal,
    var wildlifeToken: WildlifeToken?,
    val terrains: Terrain
) {
}