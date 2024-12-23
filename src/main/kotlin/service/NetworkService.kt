package service



import edu.udo.cs.sopra.ntf.entity.ScoringCards
import entity.*
import entity.Animal as LocalAnimal
import edu.udo.cs.sopra.ntf.messages.*
import java.lang.IllegalStateException

class NetworkService (private  val rootService: RootService) : AbstractRefreshingService() {

    companion object {
        /** URL of the BGW net server hosted for SoPra participants */
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        /** Name of the game as registered with the server */
        const val GAME_ID = "Cascadia"
    }

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

        client?.joinGame(sessionID, "Hello!")

        updateConnectionState(ConnectionState.GUEST_WAITING_FOR_CONFIRMATION)
    }

    fun startNewHostedGame(players: List<String>) {
        check(connectionState == ConnectionState.WAITING_FOR_GUESTS)
        { "currently not prepared to start a new hosted game." }
        //if( index == 0) {
        //    updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        //}else{
        //    updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        //}

    }
    fun startNewJoinedGame(message: GameInitMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT) {
            "Not waiting for game init message."
        }

        val players = message.playerList.map { playerName ->
            Player(
                name = playerName,
                habitat = mutableMapOf(),
                playerType = PlayerType.NETWORK
            )
        }.toMutableList()
        val playerNames = players.map { it.name }
        val scoreRules = message.gameRules.map { (_, scoringCard) ->
            when (scoringCard) {
                ScoringCards.A -> false
                ScoringCards.B -> true
            }
        }
        val index = playerNames.indexOf(client!!.playerName)

        rootService.gameService.startNewGame(
            playerNames = players.associate { it.name to it.playerType },
            scoreRules = scoreRules
        )

        if (index == 0) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }

    }
    fun resolvedOverPopulationMessage(message: ResolveOverpopulationMessage, sender: String) {

        updateConnectionState(ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS)

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
                    val indices = shopTokens
                        .mapIndexedNotNull { index, token -> if (token?.animal == animal) index else null }
                        .take(4)
                    rootService.playerActionService.replaceWildlifeTokens(indices)
                }
                3 -> {
                    val indices = shopTokens
                        .mapIndexedNotNull { index, token -> if (token?.animal == animal) index else null }
                        .take(3)
                    rootService.playerActionService.replaceWildlifeTokens(indices)
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

        game.wildlifeTokenList = message.wildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()

        println("Wildlife-Tokens wurden neu gemischt und übernommen:")
        println(game.wildlifeTokenList.joinToString { it.animal.toString() })

        onAllRefreshables { refreshAfterWildlifeTokenReplaced() }
    }


    fun placedMessage(message: PlaceMessage, sender: String) {
        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("Der Sender ist nicht der aktuelle Spieler.")
        }
        val qCoordTile = requireNotNull(message.qCoordTile) { "qCoordTile darf nicht null sein" }
        val rCoordTile = requireNotNull(message.rCoordTile) { "rCoordTile darf nicht null sein" }

        val habitatCoordinates = Pair(qCoordTile, rCoordTile)

        game.selectedTile = game.startTileList.flatten()[message.placedTile]
        repeat(message.tileRotation) { rootService.playerActionService.rotateTile(game.selectedTile!!) }
        rootService.playerActionService.addTileToHabitat(habitatCoordinates)

        //require(message.selectedToken in game.shop.indices) { "Ungültiger Token-Index: ${message.selectedToken}" }
        //val wildlifeToken = game.shop[message.selectedToken].second

        val targetTile = game.currentPlayer.habitat[habitatCoordinates]
        checkNotNull(targetTile) { "HabitatTile wurde nicht korrekt platziert." }
        //rootService.playerActionService.addToken(wildlifeToken, targetTile)

        //game.shop.removeAt(message.selectedToken)

        //game.wildlifeTokenList = message.wildlifeTokens.map { animal ->
        //    WildlifeToken(LocalAnimal.valueOf(animal.name))
        // }.toMutableList()
        if(message.usedNatureToken) {
            game.currentPlayer.natureToken -= 1
        }
        updateConnectionState(ConnectionState.PLAYING_MY_TURN)
    }

    fun swappedWithNatureTokenMessage(message: SwappedWithNatureTokenMessage, sender: String){
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("Der Sender ist nicht der aktuelle Spieler.")
        }
        message.selectedTokens.forEach { index ->
            require(index in game.shop.indices) { "Ungültiger Token-Index: $index" }
        }
        message.selectedTokens.forEachIndexed { listIndex, shopIndex ->
            val newWildlifeToken = game.wildlifeTokenList[listIndex]
            game.shop[shopIndex] = Pair(game.shop[shopIndex].first, newWildlifeToken)
        }

        // Beutel des Spiels durch den Beutel aus der Nachricht aktualisieren
        //game.wildlifeTokenList = message.wildlifeTokens.map { animal ->
        //    WildlifeToken(entity.Animal.valueOf(animal.name))
        //}.toMutableList()

    }
    fun sendPlacedMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
    }
    fun sendSwappedWithNatureTokenMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
    }
    fun sendResolvedOverPopulationMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS) { "not my turn" }
    }
    fun sendShuffledWildlifeTokensMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS)
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