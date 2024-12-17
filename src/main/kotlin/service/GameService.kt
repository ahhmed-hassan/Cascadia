package service

import entity.PlayerType

/**
 *  Service class for all actions that must be handled by the game itself.
 *
 *  @param [rootService] the games RootService for communication with entity layer
 */
class GameService(private val rootService : RootService) : AbstractRefreshingService() {

    /**
     *
     */
    fun startNewGame(playerNames : Map<String,PlayerType>, scoreRules : List<Boolean>) {
        //ToDo

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     *  End a turn of a [CascadiaGame] by refilling the shop
     *  and switching the [currentPlayer] to the next player in [PlayerList].
     *  Resolve all occurring overpopulations of four that appear during the shop refill.
     *  Call a GUI-refresh afterwards.
     *
     *  If the [habitatTileStack] is empty prior to the shop refill
     *  [nextTurn] will end the game by calling a GUI-refresh.
     *
     *  @throws IllegalStateException if player has not yet added a tile to his habitat
     *
     */
    fun nextTurn() {

        // check prerequisites
        // check for existing game
        val game = rootService.currentGame
        checkNotNull(game)


        // check if player performed action
        check(!game.hasPlayedTile) { "Player must at least add a habitat tile each turn" }

        // check for game end and if so, update GUI
        if (game.habitatTileList.size == 0) {
            onAllRefreshables { refreshAfterGameEnd() }
            return
        }

        // refill shop
        val newHabitatTile = game.habitatTileList[game.habitatTileList.size-1]
        val newWildlifeToken = game.wildlifeTokenList[game.wildlifeTokenList.size-1]
        for (i in 0 until game.shop.size) {
            // refill missing pair
            if(game.shop[i].first == null && game.shop[i].second == null) {
                game.shop[i] = Pair(newHabitatTile, newWildlifeToken)
            }
            // refill missing habitatTile from custom pair
            else if (game.shop[i].first == null) {
                game.shop[i] = Pair(newHabitatTile, game.shop[i].second)
            }
            // refill missing wildlifeToken from custom pair
            else if (game.shop[i].second == null) {
                game.shop[i] = Pair(game.shop[i].first, newWildlifeToken)
            }
        }

        // check for and resolve possible overpopulation of four
        if(checkForSameAnimal()) {
            resolveOverpopulation()
        }

        // switch current player
        val nextPlayerIndex = game.playerList.indexOf(game.currentPlayer)+1 % game.playerList.size
        game.currentPlayer = game.playerList[nextPlayerIndex]

        // refresh GUI
        onAllRefreshables { refreshAfterNextTurn() }
    }

    /**
     *
     */
    fun resolveOverpopulation() {

    }

    /**
     *
     */
    internal fun checkForSameAnimal( tokenIndices : List<Int> = listOf(0,1,2,3) ): Boolean {
        return true
    }

}