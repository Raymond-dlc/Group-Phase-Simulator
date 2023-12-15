package com.oceanscurse.groupstagesimulator.ui.teams

import com.oceanscurse.groupstagesimulator.model.Team

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class TeamsUiState(
    val isComplete: Boolean = false,
    val teams: MutableList<Team> = mutableListOf(),
    val totalTeams: Int = 0

)