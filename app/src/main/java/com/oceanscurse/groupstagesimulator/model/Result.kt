package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class Result(
    var position: Int,
    var team: Team,
    var timesPlayed: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var netScore: Int = 0,
    var goals: Int = 0,
    var goalsAgainst: Int = 0,
    var points: Int = 0,
)
