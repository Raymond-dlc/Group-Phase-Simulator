package com.oceanscurse.groupstagesimulator.ui.teams

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TeamsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeamsUiState()
    )
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()


    /**
     * Refreshes the team with a copy of the uiState, to make sure the UI updates.
     */
    fun refreshTeams() {
        val teams = TeamsRepository.getTeams()
        _uiState.update {
            it.copy(teams = teams)
        }
    }

    /**
     * Will create teams instantly, to more quickly get started with the simulation.
     * The number of team depends on the constant NUM_COMPETING_TEAMS
     * Depending on if the team already exists, it will be overridden.
     */
    fun createTeams() {
        for (i in 0 until Constants.NUM_COMPETING_TEAMS) {
            TeamsRepository.createFullTeam(i)
        }
        refreshTeams()
    }
}