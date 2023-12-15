package com.oceanscurse.groupstagesimulator.ui.teams

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.R
import com.oceanscurse.groupstagesimulator.data.PlayersRepository
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TeamDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        TeamDetailsUiState(
            players = PlayersRepository.getPlayersForTeam(0),
            isComplete = TeamsRepository.getTeams().size > 3
        )
    )
    val uiState: StateFlow<TeamDetailsUiState> = _uiState.asStateFlow()

    private var mCurrentLogoIndex = 0
    private val mLogosResourceIds = mutableListOf(
        R.drawable.logo_1,
        R.drawable.logo_2,
        R.drawable.logo_3,
        R.drawable.logo_4,
        R.drawable.logo_5,
        R.drawable.logo_6,
        R.drawable.logo_7,
        R.drawable.logo_8,
        R.drawable.logo_9
    )

    /**
     * Will select the previous logo in the list of logos the user can choose form.
     */
    fun selectPreviousLogo() {
        mCurrentLogoIndex++
        if (mCurrentLogoIndex >= mLogosResourceIds.size) {
            mCurrentLogoIndex = 0
        }
        updateLogo()
    }

    /**
     * Will select the next logo in the list of logos the user can choose form.
     */
    fun selectNextLogo() {
        mCurrentLogoIndex--
        if (mCurrentLogoIndex <= 0) {
            mCurrentLogoIndex = mLogosResourceIds.size - 1
        }
        updateLogo()
    }

    /**
     * Will attempt to save the team to the repository.
     * Will only succeed if:
     * - Team name is not empty.
     * - Player count is 11.
     */
    fun saveTeam(teamName: String) {
        println("save team : $teamName")
    }

    /**
     * Will update the uiState with the current selected logo index.
     */
    private fun updateLogo() {
        _uiState.update {
            it.copy(logoResourceId = mLogosResourceIds[mCurrentLogoIndex])
        }
    }

    /**
     * Adds a set of 11 random players to the team.
     */
    fun randomizePlayers() {

    }


}