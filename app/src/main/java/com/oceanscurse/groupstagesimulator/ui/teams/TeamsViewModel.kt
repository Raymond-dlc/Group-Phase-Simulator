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
        TeamsUiState(
            teams = TeamsRepository.getTeams(),
            isComplete = TeamsRepository.getTeams().size > 3
        )
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
}