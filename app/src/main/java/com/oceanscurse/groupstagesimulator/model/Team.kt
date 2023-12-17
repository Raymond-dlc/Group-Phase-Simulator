package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class Team(
    val id: Int,
    val name: String,
    val logoResourceId: Int,
    val players: List<Player>
) {
    fun isComplete(): Boolean = name.isNotBlank() && players.isNotEmpty()
    fun totalStrength(): Int = players.sumOf { it.strength }
    fun totalSpeed(): Int = players.sumOf { it.speed }
    fun totalDefence(): Int = players.sumOf { it.defence }
    fun totalTeamPoints(): Int = players.sumOf { it.strength + it.speed + it.defence }
}
