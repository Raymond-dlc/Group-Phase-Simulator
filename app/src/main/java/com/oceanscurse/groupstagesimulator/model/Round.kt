package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * A round that can be played, it contains a list of matches and a
 * flag to indicate if the round has already been played.
 */
data class Round(

    /**
     * Id of the round.
     */
    val id: Int,

    /**
     * The matches that will be played in this round.
     */
    val matches: List<Match>,

    /**
     * Whether or not the round has already been played.
     */
    var isPlayed: Boolean = false
)

/**
 * Will return true if all matches have a home team and away team assigned to all matches.
 */
fun Round.hasTeamsAssigned(): Boolean {
    return matches.all { it.homeTeam != null && it.awayTeam != null }
}