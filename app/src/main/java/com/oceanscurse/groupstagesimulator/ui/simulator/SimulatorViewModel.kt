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
                assignTeams()
            }
        }

        _uiState.update {
            it.copy(
                groupStage = mGroupStage,
                meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS
            )
        }
    }

    /**
     * Generates rounds for the current groupStage. It will create as many rounds as defined
     * in Constants.NUM_ROUNDS and as many matches as defined in Constants.NUM_MATCHES_PER_ROUND.
     * The rounds will start off empty, with no teams assigned to the matches.
     */
    private fun generateRounds() {
        val rounds = mutableListOf<Round>()
        for (i in 0 until Constants.NUM_ROUNDS) {
            val matches = mutableListOf<Match>()
            for (j in 0 until Constants.NUM_MATCHES_PER_ROUND) {
                matches.add(Match())
            }
            rounds.add(Round(i, matches))
        }
        mGroupStage.rounds = rounds
    }

    /**
     * Assigns the teams to the rounds, preparing them for the play-offs.
     * Does so by shuffling the teams, and assigning them
     */
    fun assignTeams() {
        val teams = TeamsRepository.getTeams()

        mGroupStage.rounds.forEach { round ->
            teams.shuffle()
            round.matches.forEachIndexed { index, match ->
                match.homeTeam = teams[(index * 2) % Constants.NUM_COMPETING_TEAMS]
                match.awayTeam = teams[((index * 2) + 1) % Constants.NUM_COMPETING_TEAMS]
            }
        }
    }

}