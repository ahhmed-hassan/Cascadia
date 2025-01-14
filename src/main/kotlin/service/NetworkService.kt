package service

import edu.udo.cs.sopra.ntf.entity.Animal
import edu.udo.cs.sopra.ntf.entity.ScoringCards
import entity.*
import entity.Animal as LocalAnimal
import edu.udo.cs.sopra.ntf.messages.*
import edu.udo.cs.sopra.ntf.entity.Animal as RemoteAnimal
import java.lang.IllegalStateException

/**
* Service layer class that realizes the necessary logic for sending and receiving messages
* in multiplayer network games.
*/
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
        this.playersList.add(name)
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

    /**
     * Starts a new hosted game with the given configuration parameters.
     *
     * This function initializes a new game session as the host, setting up the game rules,
     * player order, and order of startTiles. It also ensures that the system is in the correct
     * connection state to host a game and updates the connection state based on the player's turn.
     *
     * @param orderIsRanom Indicates whether the player order should be randomized.
     * @param isRandomRules Indicates whether the game rules should be randomized.
     * @param scoreRules A list of boolean values defining the scoring rules for the game.
     *                   ( [true] for B [false] for A).
     *
     * @throws IllegalStateException if the connection state is not `WAITING_FOR_GUESTS`,
     *                               indicating that the system is not prepared to start a new game.
     * @throws IllegalStateException if `currentGame` or `client` is null, ensuring that
     *                               the game and client are properly initialized.
     *
     */
    fun startNewHostedGame(orderIsRanom : Boolean, isRandomRules : Boolean, scoreRules : List<Boolean>) {
        check(connectionState == ConnectionState.WAITING_FOR_GUESTS)
        { "currently not prepared to start a new hosted game." }

        val players = this.playersList
        val playerNames = players.associateWith { PlayerType.NETWORK }

        rootService.gameService.startNewGame(
            playerNames,
            scoreRules,
            orderIsRanom,
            isRandomRules,
            null,
        )

        sendGameInitMessage()

        val game = rootService.currentGame
        checkNotNull(game)

        val networkClient = checkNotNull(client)
        // Check if client is the first player to play
        val index = players.indexOf(networkClient.playerName)
        if( index == 0) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else{
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }

    }

    /**
     * Starts a new game session as a joined player using the provided game initialization message.
     *
     * This function processes the `GameInitMessage` received when joining a game, setting up
     * the player list, game rules, and initial game state. It also ensures the correct
     * connection state before starting the game and updates the connection state based on
     * the player's turn.
     *
     * @param message The game initialization message containing the necessary details for setting up
     *                the game, including the player list, starting tiles, game rules, and wildlife tokens.
     *
     * @throws IllegalStateException if the connection state is not `WAITING_FOR_INIT`, indicating that
     *                               the system is not ready to process the game initialization message.
     * @throws IllegalStateException if no client is connected, ensuring the client object is properly initialized.
     *
     */
    fun startNewJoinedGame(message: edu.udo.cs.sopra.ntf.messages.GameInitMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT) {
            "Not waiting for game init message."
        }

        val playerNames = message.playerList.associateWith { PlayerType.NETWORK }

        val startTilesOrder = message.startTiles

        // Mapping gameRules to a boolean list (A is false, B is ture)
        val scoreRules = message.gameRules.map { (_, scoringCard) ->
            scoringCard == ScoringCards.B
        }

        val client = checkNotNull(client){"No client connected."}

        val index = message.playerList.indexOf(client.playerName)

        rootService.gameService.startNewGame(
            playerNames = playerNames,
            scoreRules = scoreRules,
            orderIsRandom = false,
            isRandomRules = false,
            startTileOrder = startTilesOrder,
        )

        // initializing the list of Wildlife-Tokens
        rootService.currentGame?.wildlifeTokenList = message.initWildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()
        val allHabitatTiles = rootService.gameService.getHabitatTiles()
        val newHabitatTileList = message.habitatTileList.mapNotNull { id ->
            allHabitatTiles.find { it.id == id } // Finde das Tile mit der entsprechenden ID
        }
        val currentGame = checkNotNull(rootService.currentGame)
        currentGame.habitatTileList = newHabitatTileList.toMutableList()
        currentGame.shop?.clear()
        currentGame.let { game ->
            val shop = game.habitatTileList.take(4).mapIndexed { index, tile ->
                (tile ?: null) to game.wildlifeTokenList.getOrNull(index)
            }.toMutableList()
            game.shop = shop
        }

        //Remove the used habitat tiles and wildlife tokens from the main list.
        currentGame.habitatTileList.removeAll(currentGame.shop.map { it.first })
        currentGame.wildlifeTokenList.removeAll(currentGame.shop.map { it.second })

        // Check if current player is the first player to play
        if (index == 0) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }

    }

    /**
     * Processes a `ResolveOverpopulationMessage` to handle overpopulation scenarios in the game.
     *
     * This function ensures that the correct player has sent the message and processes overpopulation
     * events based on the tokens present in the shop. Depending on the number of matching wildlife tokens,
     * it resolves the overpopulation of 3 or 4.
     *
     * @param message The message containing details about the overpopulation event.
     * @param sender The name of the player who sent the message.
     *
     * @throws IllegalStateException if there is no current game or the sender is not the current player.
     *
     */
    fun resolvedOverPopulationMessage(sender: String) {

        val game = rootService.currentGame
        checkNotNull(game) { "there is no active game." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("the sender is not the current player.")
        }

        // Extract the wildlife tokens currently present in the shop.
        val shopTokens = game.shop.map { it.second }

        // Group the tokens by their animal type and count the occurrences of each type.
        val animalCount = shopTokens.groupingBy { it?.animal }.eachCount()

        // Find the animal type with the highest occurrence in the shop.
        val mostFrequentAnimal = animalCount.maxByOrNull { it.value }

        if (mostFrequentAnimal != null) {
            val animal = mostFrequentAnimal.key // The animal type with the highest count.
            val count = mostFrequentAnimal.value // The count of the most frequent animal.

            when (count) {
                4 -> {
                    // If there are 4 tokens of the same animal type, trigger the resolution of overpopulation.
                    rootService.gameService.resolveOverpopulation()
                }
                3 -> {
                    // If there are 3 tokens of the same animal type, identify their indices in the shop.
                    val indices = shopTokens
                        .mapIndexedNotNull { index, token ->
                            if (token?.animal == animal) index else null
                        }
                        .take(3) // Limit the list to the first 3 matching tokens.

                    // Execute the replacement of the identified tokens in the shop.
                    rootService.gameService.executeTokenReplacement(indices,
                        true,
                        false)
                }
            }
        }

        updateConnectionState(ConnectionState.OPPONENT_SWAPPING_WILDLIFE_TOKENS)
    }

    /**
     * Handles a `ShuffleWildlifeTokensMessage` to update the game's wildlife token list
     * after the tokens have been shuffled by the current player.
     *
     * @param message The message containing the shuffled wildlife tokens.
     * @param sender The name of the player who sent the shuffle message.
     *
     * @throws IllegalStateException if there is no active game or if the sender is not the current player.
     */
    fun shuffledWildlifeTokensMessage(message: ShuffleWildlifeTokensMessage, sender: String) {
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)


        val game = rootService.currentGame
        checkNotNull(game) { "there is no active game." }
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("the sender is not the current player")
        }
        game.wildlifeTokenList = message.shuffledWildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()

    }

    /**
     * Handles a `PlaceMessage` to execute the placement of a tile and optionally a token
     * in the game. This function validates the message, executes the placement actions,
     * and updates the game state accordingly.
     *
     * @param message The message containing details about the tile and token placement,
     *                including coordinates, rotation, and whether a nature token is used.
     * @param sender The name of the player who sent the placement message.
     *
     * @throws IllegalStateException if there is no active game or the sender is not the current player.
     * @throws IllegalArgumentException if required coordinates or indices are invalid.
     */
    fun placedMessage(message: PlaceMessage, sender: String) {
        // Ensure there is an active game.
        val game = rootService.currentGame
        checkNotNull(game) { "there is no active game." }

        // Verify that the sender is the current player.
        if (game.currentPlayer.name != sender) {
            throw IllegalStateException("the sender is not the current player.")
        }

        // Ensure the placed tile is within valid indices.
        check(message.placedTile in game.shop.indices)
        game.shop[message.placedTile].first?.wildlifeToken = null

        // Validate and extract tile coordinates.
        val qCoordTile = requireNotNull(message.qcoordTile) { "qCoordTile must not be null" }
        val rCoordTile = requireNotNull(message.rcoordTile) { "rCoordTile must not be null" }
        val habitatCoordinates = Pair(qCoordTile, rCoordTile)

        if (message.selectedToken != null) {
            // Validate the selected token's index and coordinates if a token is provided.
            check(message.selectedToken in game.shop.indices)
            val qCoordToken = requireNotNull(message.qcoordToken) { "qCoordTile must not be null" }
            val rCoordToken = requireNotNull(message.rcoordToken) { "rCoordTile must not be null" }
            val wildlifeTokenCoordinates = Pair(qCoordToken, rCoordToken)

            if (message.usedNatureToken) {
                // Execute actions if a nature token is used.
                rootService.playerActionService.chooseCustomPair(message.placedTile, message.selectedToken)
                repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
                rootService.playerActionService.addTileToHabitat(habitatCoordinates)
                val targetTile = game.currentPlayer.habitat[wildlifeTokenCoordinates]
                checkNotNull(targetTile)
                rootService.playerActionService.addToken(targetTile)
            } else {
                // Execute actions if no nature token is used.
                rootService.playerActionService.chooseTokenTilePair(message.placedTile)
                repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
                rootService.playerActionService.addTileToHabitat(habitatCoordinates)
                val targetTile = game.currentPlayer.habitat[habitatCoordinates]
                checkNotNull(targetTile)
                rootService.playerActionService.addToken(targetTile)
            }
        } else {
            // Execute actions when no token is selected.
            rootService.playerActionService.chooseTokenTilePair(message.placedTile)
            repeat(message.tileRotation) { rootService.playerActionService.rotateTile() }
            rootService.playerActionService.addTileToHabitat(habitatCoordinates)
            rootService.playerActionService.discardToken()
        }

        // Update the game's wildlife token list using the tokens from the message.
        game.wildlifeTokenList = message.wildlifeTokens.map { animal ->
            WildlifeToken(LocalAnimal.valueOf(animal.name))
        }.toMutableList()

        // Update the connection state based on the current player.
        if (client?.playerName == game.currentPlayer.name) {
            updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }
    }

    /**
     * Handles a `SwappedWithNatureTokenMessage` to process the swapping of wildlife tokens
     * using a nature token. This function validates the input message, updates the game state,
     * and replaces tokens as specified.
     *
     * @param message The message containing the indices of the tokens to be swapped and the updated
     *                wildlife token list.
     * @param sender The name of the player who sent the swap message.
     *
     * @throws IllegalStateException if there is no active game.
     * @throws IllegalArgumentException if the sender is not the current player or if any token index is invalid.
     */
    fun swappedWithNatureTokenMessage(message: SwappedWithNatureTokenMessage, sender: String) {
        // Update the connection state to indicate it's the opponent's turn.
        updateConnectionState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        // Ensure there is an active game.
        val game = checkNotNull(rootService.currentGame) { "No active game available." }

        // Verify that the sender is the current player.
        require(game.currentPlayer.name == sender) { "The sender is not the current player." }

        // Validate that all token indices are within valid bounds.
        message.swappedSelectedTokens.forEach { index ->
            require(index in game.shop.indices) { "Invalid token index: $index" }
        }

        require(game.currentPlayer.natureToken > 0)
        game.currentPlayer.natureToken--

        // Execute token replacement using the provided indices, marking the nature token as used.
        rootService.gameService.executeTokenReplacement(message.swappedSelectedTokens,
            true,
            true)

        // Update the game's wildlife token list with the swapped tokens from the message.
        game.wildlifeTokenList = message.swappedWildlifeTokens.map { animal ->
            WildlifeToken(entity.Animal.valueOf(animal.name))
        }.toMutableList()
    }

    /**
     * Sends a `GameInitMessage` to initialize the game for all players in a networked session.
     *
     * This function gathers all necessary game state information and constructs a
     * `GameInitMessage`, which is then sent to the connected network client. The message
     * includes habitat tile IDs, player names, game rules, starting tile order, and the list of
     * wildlife tokens.
     *
     * @throws IllegalStateException if there is no active game or if no network client is found.
     */
    private fun sendGameInitMessage() {
        // Ensure there is an active game and retrieve the current game instance.
        val game = checkNotNull(rootService.currentGame) { "Game not found" }
        val shopTileIds = game.shop.map { it.first?.id }
        val shopTokenAnimals = game.shop.map { it.second?.animal }
        val shop = shopTokenAnimals.map { token -> token?.let { Animal.valueOf(it.name) } }
        // Extract IDs of habitat tiles.
        val habitateTileIds = shopTileIds.filterNotNull() + game.habitatTileList.map { it.id }

        // Extract player names from the player list.
        val playerNames = game.playerList.map { it.name }

        // Map game rules to their corresponding scoring cards (A - false or B - true).
        val gameRules = Animal.values().zip(
            game.ruleSet.map { if (it) ScoringCards.B else ScoringCards.A }
        ).toMap()

        // Generate the starting tile order based on the number of players.
        val startTiles = (1..playerNames.size).toList()

        // Extract wildlife tokens from the game and map them to their animal type.
        val wildLifeTokens = shop.filterNotNull() + game.wildlifeTokenList.map { token ->
            Animal.valueOf(token.animal.name)
        }

        // Construct the `GameInitMessage` with the extracted information.
        val message = GameInitMessage(
            habitateTileIds,
            playerNames,
            gameRules,
            startTiles,
            wildLifeTokens
        )

        // Ensure there is a network client and send the game initialization message.
        val networkClient = checkNotNull(client) { "No network client found" }
        networkClient.sendGameActionMessage(message)
    }

    /**
     * Sends a `PlaceMessage` to indicate the placement of a tile and a token during the player's turn.
     *
     * This function constructs and sends a `PlaceMessage` to the connected network client,
     * ensuring that all required placement details, including tile and token coordinates,
     * rotation, and wildlife tokens, are included. After sending the message, the relevant
     * state variables are reset to their default values.
     *
     *
     * @throws IllegalStateException if the connection state is not `PLAYING_MY_TURN`.
     * @throws IllegalArgumentException if the game, tile placement index, or tile coordinates are null.
     *
     */
    fun sendPlacedMessage() {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }

        val game = checkNotNull(rootService.currentGame) { "Game not found" }
        val tileIndex = requireNotNull(placedTileIndex)
        val coordinates = requireNotNull(tileCoordinates) { "Tile coordinates must not be null" }
        val qTile = coordinates.first
        val rTile = coordinates.second

        val tokenIndex = requireNotNull(selectedTokenIndex)
        val qToken = tokenCoordinates?.first ?: -1 // Default-Wert, wenn null
        val rToken = tokenCoordinates?.second ?: -1 // Default-Wert, wenn null

        val wildlifeTokensList = game.wildlifeTokenList.map { RemoteAnimal.valueOf(it.animal.name) }

        val message = PlaceMessage (
            placedTile = tileIndex,
            qcoordTile = qTile,
            rcoordTile = rTile,
            selectedToken = tokenIndex,
            qcoordToken = qToken,
            rcoordToken = rToken,
            usedNatureToken = usedNatureToken,
            tileRotation = tileRotation,
            wildlifeTokens = wildlifeTokensList,
        )

        // Ensure there is a network client and send the message.
        val networkClient = checkNotNull(client) { "No network client found" }
        networkClient.sendGameActionMessage(message)

        // reset to default
        placedTileIndex = null
        tileCoordinates = null
        selectedTokenIndex = null
        tokenCoordinates = null
        tileRotation = 0
        usedNatureToken = false


    }

    /**
     * Sends a `SwappedWithNatureTokenMessage` to indicate the swapping of selected tokens
     * using a nature token during the player's turn.
     *
     * This function constructs and sends a `SwappedWithNatureTokenMessage` to the connected
     * network client, including the indices of the swapped tokens and the current wildlife token list.
     *
     * @param indices A list of integers representing the indices of the tokens to be swapped.
     *
     * @throws IllegalStateException if the connection state is not `PLAYING_MY_TURN`.
     * @throws IllegalArgumentException if the game is not active or no network client is found.
     */
    fun sendSwappedWithNatureTokenMessage(indices : List<Int>) {
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }
        val game = checkNotNull(rootService.currentGame) { "Game not found" }

        val wildlifeTokensList = game.wildlifeTokenList.map { RemoteAnimal.valueOf(it.animal.name) }

        val message = SwappedWithNatureTokenMessage(indices, wildlifeTokensList)
        // Ensure there is a network client and send the message.

        val networkClient = checkNotNull(client) { "No network client found" }
        networkClient.sendGameActionMessage(message)
    }

    /**
     * Sends a `ResolveOverpopulationMessage` to indicate that the current player has resolved an overpopulation
     * scenario.
     *
     * This function constructs and sends a `ResolveOverpopulationMessage` to the connected network client.
     * It ensures that the current connection state is appropriate for resolving overpopulation
     * and that a network client exists.
     *
     * @throws IllegalStateException if the connection state is not `SWAPPING_WILDLIFE_TOKENS`.
     * @throws IllegalArgumentException if no network client is found.
     */
    fun sendResolvedOverPopulationMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS ||
                connectionState == ConnectionState.PLAYING_MY_TURN) { "not my turn" }

        if (connectionState == ConnectionState.PLAYING_MY_TURN) {
            updateConnectionState(ConnectionState.SWAPPING_WILDLIFE_TOKENS)
        }
        val message = ResolveOverpopulationMessage()

        // Ensure there is a network client and send the message.
        val networkClient = checkNotNull(client) { "No network client found" }
        networkClient.sendGameActionMessage(message)


    }

    /**
     * Sends a `ShuffleWildlifeTokensMessage` to indicate that the current player has shuffled the wildlife tokens.
     *
     * This function constructs and sends a `ShuffleWildlifeTokensMessage` to the connected network client.
     * It ensures that the connection state is appropriate and that the current game and network client are available.
     *
     * @throws IllegalStateException if the connection state is not `SWAPPING_WILDLIFE_TOKENS`.
     * @throws IllegalArgumentException if no active game is found or if no network client is available.
     */
    fun sendShuffledWildlifeTokensMessage() {
        require(connectionState == ConnectionState.SWAPPING_WILDLIFE_TOKENS)

        val game = rootService.currentGame
        checkNotNull(game) { "Kein aktuelles Spiel vorhanden." }

        val message = ShuffleWildlifeTokensMessage(
            game.wildlifeTokenList.map { RemoteAnimal.valueOf(it.animal.name) } // Konvertiert zu List<Animal>
        )

        // Ensure there is a network client and send the message.
        val networkClient = checkNotNull(client) { "No network client found" }
        networkClient.sendGameActionMessage(message)
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
     * Updates the GUI to reflect the current list of players when a new player joins.
     *
     * This function invokes the `refreshAfterPlayerJoined` method on all refreshable components,
     * ensuring that the GUI is updated to display the most recent player list.
     *
     * @param playerList A mutable list of strings representing the names of all players in the game.
     */
    fun refreshPlayerList(playerList : MutableList<String>) {
        onAllRefreshables {
            refreshAfterPlayerJoined(playerList)
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
     * all refreshables via Refreshables.refreshConnectionState
     */
    fun updateConnectionState(newState: ConnectionState) {
        this.connectionState = newState
        onAllRefreshables {
            refreshConnectionState(newState)
        }
    }

}