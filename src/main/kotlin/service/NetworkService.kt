package service



import entity.*
import edu.udo.cs.sopra.ntf.*
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
        updateConnectionState(ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
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

        updateConnectionState(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
    }

    fun startNewHostedGame(players: List<String>) {
        updateConnectionState(ConnectionState.PLAYING_MY_TURN)
    }
    fun startNewJoinedGame(message: GameInitMessage) {
        updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
    }
    fun resolvedOverPopulationMessage(message: ResolveOverpopulationMessage, sender: String) {
        updateConnectionState(ConnectionState.OPPONENT_SWAPPING_WILDLIFETOKEN)
    }
    fun shuffledWildlifeTokensMessage(message: ShuffleWilflifeTokensMessage, sender: String) {
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
    }
    fun placedMessage(message: PlaceMessage, sender: String) {
        updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
    }
    fun swappedWithNatureTokenMessage(message: SwapWithNatureTokenMessage, sender: String){
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENT)
    }
    fun sendPlacedMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
    }
    fun sendSwappedWithNatureTokenMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
    }
    fun sendResolvedOverPopulationMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILFLIFE_TOKENS) { "not my turn" }
    }
    fun sendShuffledWildlifeTokensMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILFLIFE_TOKENS)
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
            refreshConnectionState(newState)
        }
    }

}