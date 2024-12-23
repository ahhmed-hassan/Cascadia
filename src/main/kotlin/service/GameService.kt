package service

import entity.Animal
import entity.HabitatTile
import entity.PlayerType

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun startNewGame(playerNames: Map<String, PlayerType>, scoreRules: List<Boolean>) {
        //ToDo

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     *
     */
    fun nextTurn() {
        //ToDo

        onAllRefreshables { refreshAfterNextTurn() }
    }

    /**
     * Creates a List of all Coordinates where the current Player can place a tile
     *
     * @return List<Pair<Int,Int>>
     */
    fun getAllPossibleCoordinatesForTilePlacing(): List<Pair<Int, Int>> {
        val coordinates = hashSetOf<Pair<Int, Int>>()
        val habitat = rootService.currentGame?.currentPlayer?.habitat
        checkNotNull(habitat)

        habitat.forEach {
            val key = it.key
            if (habitat[Pair(key.first + 1, key.second - 1)] == null) coordinates.add(
                Pair(
                    key.first + 1,
                    key.second - 1
                )
            )
            if (habitat[Pair(key.first, key.second - 1)] == null) coordinates.add(
                Pair(
                    key.first,
                    key.second - 1
                )
            )
            if (habitat[Pair(key.first - 1, key.second)] == null) coordinates.add(
                Pair(
                    key.first - 1,
                    key.second
                )
            )
            if (habitat[Pair(key.first - 1, key.second + 1)] == null) coordinates.add(
                Pair(
                    key.first - 1,
                    key.second + 1
                )
            )
            if (habitat[Pair(key.first, key.second + 1)] == null) coordinates.add(
                Pair(
                    key.first,
                    key.second + 1
                )
            )
            if (habitat[Pair(key.first + 1, key.second)] == null) coordinates.add(
                Pair(
                    key.first + 1,
                    key.second
                )
            )
        }
        return coordinates.toList()
    }

    /**
     * Creates a List of all HabitatTiles, where the current Player can place the given Animal
     *
     * @param animal the animal Type
     * @return List<HabitatTile>
     */
    fun getAllPossibleTilesForWildlife(animal: Animal): List<HabitatTile> {
        val habitat = rootService.currentGame?.currentPlayer?.habitat
        checkNotNull(habitat)

        return habitat.values.filter { it.wildlifeToken == null && it.wildlifeSymbols.contains(animal) }
    }
}