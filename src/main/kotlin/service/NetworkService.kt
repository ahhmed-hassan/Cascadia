package service



import edu.udo.cs.sopra.ntf.entity.Animal
import edu.udo.cs.sopra.ntf.entity.ScoringCards
import entity.*
import entity.Animal as LocalAnimal
import edu.udo.cs.sopra.ntf.messages.*
import edu.udo.cs.sopra.ntf.entity.Animal as RemoteAnimal
import java.lang.IllegalStateException

class NetworkService (private  val rootService: RootService) : AbstractRefreshingService() {

    companion object {
        /** URL of the BGW net server hosted for SoPra participants */
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        /** Name of the game as registered with the server */
        const val GAME_ID = "Cascadia"
    }
    var placedTileIndex: Int? = null
    var tileCoordinates: Pair<Int, Int>? = null
    var selectedTokenIndex: Int? = null
    var tokenCoordinates: Pair<Int, Int>? = null
    var tileRotation: Int = 0
    var usedNatureToken = false


    var client: NetworkClient? = null
    var playersList: MutableList<String> = mutableListOf()
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED

    /**
     * Connects to server and creates a new game session.
     *
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the hosted session (to be used by guest on join)
     *
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun hostGame(secret: String, sessionID: String?, name: String, playerType : PlayerType) {
        if (!connect(secret, name, playerType)) {
            error("Connection failed")
        }
        updateConnectionState(ConnectionState.CONNECTED)

        if (sessionID.isNullOrBlank()) {
            client?.createGame(GAME_ID, "Welcome!")
        } else {
            client?.createGame(GAME_ID, sessionID, "Welcome!")
        }
        updateConnectionState(ConnectionState.HOST_WAITING_FOR_CONFIRMATION)
    }

    /**
     * Connects to server and joins a game session as guest player.
     *
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the joined session (as defined by host on create)
     *
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun joinGame(secret: String, name: String, sessionID: String, playerType: PlayerType ) {
        if (!connect(secret, name, playerType)) {
            error("Connection failed")
        }
        updateConnectionState(ConnectionState.CONNECTED)

        val networkClient = checkNotNull(client){"No client connected."}

        networkClient.joinGame(sessionID, "Hello!")

        updateConnectionState(ConnectionState.GUEST_WAITING_FOR_CONFIRMATION)
    }

    fun startNewHostedGame(orderIsRanom : Boolean, isRandomRules : Boolean, scoreRules : List<Boolean>) {
        check(connectionState == ConnectionState.WAITING_FOR_GUESTS)
        { "currently not prepared to start a new hosted game." }
        val players = this.playersList
        val playerNames = players.associateWith { PlayerType.NETWORK }
        rootService.gameService.startNewGame(
            playerNames,
            scoreRules,
            //orderIsRanom,
            //isRandomRules,
        )
        sendGameInitMessage()
        val game = rootService.currentGame
        checkNotNull(game)
        val networkClient = checkNotNull(client)
        val index = players.indexOf(networkClient.playerName)
        if( index == 0) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else{
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }

    }
    fun startNewJoinedGame(message: GameInitMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT) {
            "Not waiting for game init message."
        }
        val playerNames = message.playerList.associateWith { PlayerType.NETWORK }
        val startTilesOrder = message.startTiles
        val scoreRules = message.gameRules.map { (_, scoringCard) ->
            scoringCard == ScoringCards.B
        }
        val client = checkNotNull(client){"No client connected."}

        val index = message.playerList.indexOf(client.playerName)

        rootService.gameService.startNewGame(
            playerNames = playerNames,
            scoreRules = scoreRules,
            false,
            false,
            startTilesOrder = startTilesOrder,
        )

        rootService.currentGame?.wildlifeTokenList = message.initWildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()

        if (index == 0) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }

    }

    fun resolvedOverPopulationMessage(message: ResolveOverpopulationMessage, sender: String) {

        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("Der Sender ist nicht der aktuelle Spieler.")
        }

        val shopTokens = game.shop.map { it.second }

        val animalCount = shopTokens.groupingBy { it?.animal }.eachCount()
        val mostFrequentAnimal = animalCount.maxByOrNull { it.value }

        if (mostFrequentAnimal != null) {
            val animal = mostFrequentAnimal.key
            val count = mostFrequentAnimal.value

            when (count) {
                4 -> {
                    //rootService.gameService.resolveOverpopulation()
                }
                3 -> {
                    val indices = shopTokens
                        .mapIndexedNotNull { index, token -> if (token?.animal == animal) index else null }
                        .take(3)
                    //rootService.gameService.executeTokenReplacement(indices, true, false)
                }
            }
        }
        updateConnectionState(ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS)
    }

    fun shuffledWildlifeTokensMessage(message: ShuffleWildlifeTokensMessage, sender: String) {
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)


        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("Der Sender ist nicht der aktuelle Spieler.")
        }

        game.wildlifeTokenList = message.shuffledWildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()
    }
    private fun sendGameInitMessage() {
        val game = checkNotNull(rootService.currentGame) {"Game not found"}
        val habitateTileIds = game.habitatTileList.map { it.id }
        val playerNames = game.playerList.map { it.name}
        val gameRules = Animal.values().zip(game.ruleSet.map { if (it) ScoringCards.B else ScoringCards.A }).toMap()
        val startTiles = (1..playerNames.size).toList()
        val wildLifeTokens = game.wildlifeTokenList.map { token -> Animal.valueOf(token.animal.name)}
        val message = GameInitMessage(
            habitateTileIds,
            playerNames,
            gameRules,
            startTiles,
            wildLifeTokens
        )
        val networkClient = checkNotNull(client) {"No network client found" }
        networkClient.sendGameActionMessage(message)
    }

    fun placedMessage(message: PlaceMessage, sender: String) {
        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("Der Sender ist nicht der aktuelle Spieler.")
        }

        val qCoordTile = requireNotNull(message.qcoordTile) { "qCoordTile darf nicht null sein" }
        val rCoordTile = requireNotNull(message.rcoordTile) { "rCoordTile darf nicht null sein" }

        val habitatCoordinates = Pair(qCoordTile, rCoordTile)

        check(message.placedTile in game.shop.indices)


        if (message.selectedToken != null) {
            check(message.selectedToken in game.shop.indices)

            val qCoordToken = requireNotNull(message.qcoordToken) { "qCoordTile darf nicht null sein" }
            val rCoordToken = requireNotNull(message.rcoordToken) { "rCoordTile darf nicht null sein" }

            val wildlifeTokenCoordinates = Pair(qCoordToken, rCoordToken)

            if (message.usedNatureToken) {
                rootService.playerActionService.chooseCustomPair(message.placedTile, message.selectedToken)
                repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
                rootService.playerActionService.addTileToHabitat(habitatCoordinates)
                rootService.playerActionService.addToken(wildlifeTokenCoordinates)
            } else {
                rootService.playerActionService.chooseTokenTilePair(message.placedTile)
                repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
                rootService.playerActionService.addTileToHabitat(habitatCoordinates)
                rootService.playerActionService.addToken(wildlifeTokenCoordinates)
            }
        } else {
            rootService.playerActionService.chooseTokenTilePair(message.placedTile)
            repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
            rootService.playerActionService.addTileToHabitat(habitatCoordinates)
            rootService.playerActionService.discardToken()
        }


        game.wildlifeTokenList = message.wildlifeTokens.map { animal ->
           WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()

        rootService.gameService.nextTurn()

        if (client?.playerName == game.currentPlayer.name) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }
    }

    fun swappedWithNatureTokenMessage(message: SwappedWithNatureTokenMessage, sender: String){
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        val game = checkNotNull(rootService.currentGame) { "Kein aktuelles Spiel vorhanden." }
        require(game.currentPlayer.name == sender) { "Der Sender ist nicht der aktuelle Spieler." }
        message.swappedSelectedTokens.forEach { index ->
            require(index in game.shop.indices) { "Ungültiger Token-Index: $index" }
        }

        rootService.gameService.executeTokenReplacement(message.selectedTokens, true, true)


        game.wildlifeTokenList = message.swappedWildlifeTokens.map { animal ->
            WildlifeToken(entity.Animal.valueOf(animal.name))
        }.toMutableList()

    }
    fun sendPlacedMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
        //checkNotNull(placedTileIndex)
        //checkNotNull(tileCoordinates)
        //checkNotNull()
//        val message = PlaceMessage (
//            placedTile = placedTileIndex,
//            qCoordTile = tileCoordinates.first,
//            rCoordTile = tileCoordinates.second,
//            selectedToken = selectedTokenIndex,
//            qCoordToken = tokenCoordinates.first,
//            rCoordToken = tokenCoordinates.second,
//            usedNatureToken = usedNatureToken,
//            tileRotation = tileRotation,
//            wildlifeTokens = rootService.currentGame.wildlifeTokenList,
//        )
//        client?.sendGameActionMessage(message)
    }
    fun sendSwappedWithNatureTokenMessage(indices : List<Int>) {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
        //val message = SwappedWithNatureTokenMessage(indices, rootService.currentGame.wildlifeTokenList)
        //client?.sendGameActionMessage(message)
    }
    fun sendResolvedOverPopulationMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS) { "not my turn" }
        val message = ResolveOverpopulationMessage()
        client?.sendGameActionMessage(message)
    }
    fun sendShuffledWildlifeTokensMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS)
        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        val message = ShuffleWildlifeTokensMessage(
            game.wildlifeTokenList.map { RemoteAnimal.valueOf(it.animal.name) } // Konvertiert zu List<Animal>
        )
        client?.sendGameActionMessage(message)
    }

    /**
     * creates a client and connects it with the server.
     * @param secret Server secret.
     * @param name of the player.
     * @param playerType playerType of the player
     * @return true when the client connected successfully, false when not
     * @throws IllegalStateException when ConnectionState is not [ConnectionState.DISCONNECTED]
     * @throws IllegalArgumentException when secret or name is blank
     */
    fun connect(secret: String, name: String, playerType: PlayerType) : Boolean {
        require(connectionState == ConnectionState.DISCONNECTED && client == null)
        { "already connected to another game" }

        require(secret.isNotBlank()) { "server secret must be given" }
        require(name.isNotBlank()) { "player name must be given" }

        val newClient =
            NetworkClient(
                playerName = name,
                host = SERVER_ADDRESS,
                secret = secret,
                networkService = this,
                playerType = playerType
            )

        return if (newClient.connect()) {
            this.client = newClient
            // update connection state to connected.
            true
        } else {
            false
        }
    }
    fun refreshPlayerList(playerList : MutableList<String>) {
        onAllRefreshables {
            //refreshAfterPlayerJoined(playerList)
        }
    }

    /**
    * Disconnects the [client] from the server, nulls it and updates the
    * [connectionState] to [ConnectionState.DISCONNECTED]. Can safely be called
    * even if no connection is currently active.
    */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        updateConnectionState(ConnectionState.DISCONNECTED)
    }

    /**
     * Updates the [connectionState] to [newState] and notifies
     * all refreshables via [Refreshable.refreshConnectionState]
     */
    fun updateConnectionState(newState: ConnectionState) {
        this.connectionState = newState
        onAllRefreshables {
            //refreshConnectionState(newState)
        }
    }

}