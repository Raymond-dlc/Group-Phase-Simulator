package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class Match(
    val id: Int = 0,
    var homeTeam: Team? = null,
    var awayTeam: Team? = null,
    var homeTeamScore: Int = 0,
    var awayTeamScore: Int = 0,
)
