package gui

import service.AbstractRefreshingService

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */
interface Refreshables {

    /**
     *
     */
    fun refreshAfterHotSeatConfigurationChosen() {}

    /**
     *
     */
    fun refreshAfterNetworkConfigurationMenuChosen() {}

    /**
     *
     */
    fun refreshAfterNetworkJoinMenuChosen() {}

    /**
     *
     */
    fun refreshAfterNetworkJoin() {}

    /**
     *
     */
    fun refreshAfterGameStart() {}

    /**
     *
     */
    fun refreshAfterWildlifeTokenReplaced() {}

    /**
     *
     */
    fun refreshAfterTokenTilePairChosen() {}

    /**
     *
     */
    fun refreshAfterHabitatTileAdded() {}

    /**
     *
     */
    fun refreshAfterWildlifeTokenAdded() {}

    /**
     *
     */
    fun refreshAfterNextTurn() {}

    /**
     *
     */
    fun refreshAfterGameEnd(scores: Map<String, PlayerScore>) {}

    /**
     *
     */
    fun refreshAfterTileRotation() {}

}