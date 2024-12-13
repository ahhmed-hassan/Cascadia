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

        // check for current player
        val currentPlayer = game.currentPlayer
        checkNotNull(currentPlayer)

        // check if player performed action
        if (!game.hasPlayedTile) {
            throw IllegalStateException("Player must at least add a habitat tile each turn")
        }

        // check for game end and if so, update GUI
        if (game.habitatTileList.size == 0) {
            onAllRefreshables { refreshAfterGameEnd() }
            return
        }

        // refill shop
        val newHabitatTile = game.habitatTileList[game.habitatTileList.size-1]
        val newWildlifeToken = game.wildlifeTokenList[game.wildlifeTokenList.size-1]
        // refill normal pair
        if (game.shop.size != 4) {
            val newPair = Pair(newHabitatTile, newWildlifeToken)
            game.shop.add(newPair)
        }
        // refill custom pair
        else {
            for (i in 0 until game.shop.size) {
                // refill missing habitatTile
                if (game.shop[i].first == null) {
                    game.shop[i] = Pair(newHabitatTile, game.shop[i].second)
                }
                // refill missing wildlifeToken
                else if (game.shop[i].second == null) {
                    game.shop[i] = Pair(game.shop[i].first, newWildlifeToken)
                }
            }
        }

        // check for and resolve possible overpopulation of four
        var hasOverpopulation = true
        while (hasOverpopulation) {
            // check for overpopulation of four
            for (i in 1 until 4) {
                if(game.shop[i].second != game.shop[0].second) {
                    hasOverpopulation = false
                }
            }
            // resolve possible overpopulation
            if (hasOverpopulation) {
                rootService.playerActionService.replaceWildlifeTokens(tokenIndices = listOf(0, 1, 2, 3),
                                                                      playerAction = false)
            }
        }

        // switch current player
        val nextPlayerIndex = game.playerList.indexOf(currentPlayer)+1 % game.playerList.size
        game.currentPlayer = game.playerList[nextPlayerIndex]

        // refresh GUI
        onAllRefreshables { refreshAfterNextTurn() }
    }

}