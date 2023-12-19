package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * A Team that can compete in the GroupPhase.
 */
data class Team(

    /**
     * Id of the team.
     */
    val id: Int,

    /**
     * Name of the team.
     */
    val name: String,

    /**
     * Resource id of the logo assigned to the team.
     */
    val logoResourceId: Int,

    /**
     * The teams players.
     */
    val players: List<Player>
) {
    /**
     * Return true when the team has a name and players.
     */
    fun isComplete(): Boolean = name.isNotBlank() && players.isNotEmpty()

    /**
     * Return the combined strength of all the players on the team.
     */
    fun totalStrength(): Int = players.sumOf { it.strength }

    /**
     * Return the combined speed of all the players on the team.
     */
    fun totalSpeed(): Int = players.sumOf { it.speed }

    /**
     * Return the combined defence of all the players on the team.
     */
    fun totalDefence(): Int = players.sumOf { it.defence }

    /**
     * Return the combined sum of strength, speed and defence of all the players on the team.
     */
    fun totalTeamPoints(): Int = players.sumOf { it.strength + it.speed + it.defence }
}
