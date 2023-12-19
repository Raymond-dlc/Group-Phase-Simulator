package com.oceanscurse.groupstagesimulator.ui.teams

import com.oceanscurse.groupstagesimulator.model.Team

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class TeamsUiState(

    /**
     * The competing teams to display. Can be an empty list.
     */
    val teams: MutableList<Team> = mutableListOf(),
)