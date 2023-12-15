package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 * TODO: Look up UI state for -/+
 */
data class Result(
    val team: Team,
    val timesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val points: Int,
)
