package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * A Match between two teams, where one team is playing a home game.
 */
data class Match(

    /**
     * Id of this match.
     */
    val id: Int = 0,

    /**
     * The home team of this match.
     */
    var homeTeam: Team? = null,

    /**
     * The away team of this match.
     */
    var awayTeam: Team? = null,

    /**
     * The amount of times the home team has scored.
     */
    var homeTeamScore: Int = 0,

    /**
     * The amount of time the away team has scored.
     */
    var awayTeamScore: Int = 0,
)
