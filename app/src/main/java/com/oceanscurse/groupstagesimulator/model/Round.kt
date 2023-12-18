package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class Round(
    val id: Int,
    val matches: List<Match>,
    var isPlayed: Boolean = false
)

fun Round.hasTeamsAssigned(): Boolean {
    return matches.all { it.homeTeam != null && it.awayTeam != null }
}