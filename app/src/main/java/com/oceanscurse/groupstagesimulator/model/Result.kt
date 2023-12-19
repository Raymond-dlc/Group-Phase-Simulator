package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * A collection of records of a team.
 */
data class Result(

    /**
     * The position of the team in the standings.
     */
    var position: Int,

    /**
     * The team to which these results apply.
     */
    var team: Team,

    /**
     * The number of matches that the team has played.
     */
    var timesPlayed: Int = 0,

    /**
     * The number of times the team has won a match.
     */
    var wins: Int = 0,

    /**
     * The number of times the team has played a draw.
     */
    var draws: Int = 0,

    /**
     * The number of times the team has lost a match.
     */
    var losses: Int = 0,

    /**
     * The net score of the team, based on { wins - losses }.
     */
    var netScore: Int = 0,

    /**
     * The number of times this team has scored.
     */
    var goals: Int = 0,

    /**
     * The number of times other teams have scored against this team.
     */
    var goalsAgainst: Int = 0,

    /**
     * The number of points gained from playing games.
     * Win = 2 points.
     * Draw = 1 point.
     * Loss = 0 points.
     */
    var points: Int = 0,
)
