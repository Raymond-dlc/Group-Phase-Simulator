package com.oceanscurse.groupstagesimulator.ui.teams

import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Player

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class TeamDetailsUiState(
    val isComplete: Boolean = false,
    val players: MutableList<Player> = mutableListOf(),
    val logoResourceId: Int = R.drawable.logo_1,
    val teamName: String = ""
)