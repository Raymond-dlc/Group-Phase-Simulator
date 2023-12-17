package com.oceanscurse.groupstagesimulator.ui.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.GroupStage
import com.oceanscurse.groupstagesimulator.model.Match
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.ui.teams.TeamsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.round

class SimulatorViewModel : ViewModel() {
    private var mGroupStage: GroupStage = GroupStage(0, listOf(), listOf())

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()
    fun checkConditions() {
        val meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS

        if (meetsRequirements) {
            if (mGroupStage.rounds.isEmpty()) {
                generateRounds()
            }
        }

        _uiState.update {
            it.copy(
                groupStage = mGroupStage,
                meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS
            )
        }
    }

    private fun generateRounds() {
        val teams = TeamsRepository.getTeams()

        val newRounds = mutableListOf<Round>()
        for (i in 0 until Constants.NUM_ROUNDS) {
            newRounds.add(
                Round(
                    i, listOf(
                        Match(0, teams[0], teams[1], 0, 0),
                        Match(0, teams[0], teams[1], 0, 0)
                    )
                )
            )
        }
        mGroupStage.rounds = newRounds
    }
}