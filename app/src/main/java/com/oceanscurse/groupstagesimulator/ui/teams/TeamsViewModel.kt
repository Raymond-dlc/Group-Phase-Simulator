package com.oceanscurse.groupstagesimulator.ui.teams

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TeamsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeamsUiState()
    )
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    fun addTeam() {
        TeamsRepository.addTeam(Team(0, "Team A", R.drawable.logo_1, listOf()))
        _uiState.update {
            it.copy(
                teams = TeamsRepository.getTeams()
            )
        }
    }

    fun refreshTeams() {
        val teams = TeamsRepository.getTeams()
        _uiState.update {
            it.copy(
                teams = teams,
                isComplete = teams.size > 3
            )
        }
    }

    // Will create 4 team instantly, to more quickly get started with the simulation.
    fun createTeams() {
        TeamsRepository.createFullTeam(0)
        TeamsRepository.createFullTeam(1)
        TeamsRepository.createFullTeam(2)
        TeamsRepository.createFullTeam(3)
        refreshTeams()
    }
}