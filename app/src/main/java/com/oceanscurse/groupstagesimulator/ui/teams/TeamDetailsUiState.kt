package com.oceanscurse.groupstagesimulator.ui.teams

import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.model.Player
import java.lang.Exception

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class TeamDetailsUiState(
    /**
     * A flag that will indicate to the UI that the team has been saved.
     */
    val isSaved: Boolean = false,
    /**
     * A list players that belong to the team.
     */
    val players: MutableList<Player> = mutableListOf(),
    /**
     * The resource id of the logo that has been selected for the team.
     */
    val logoResourceId: Int = R.drawable.logo_1,
    /**
     * The name of the team.
     */
    val teamName: String = "",

    /**
     * If any exceptions occur, they will be set here. This can be:
     * - NameEmptyException => When the team name is not empty.
     * - TeamEmptyException => When the player count is 11.
     */
    val saveException: Exception? = null
)