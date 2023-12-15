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
}
