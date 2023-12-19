package com.oceanscurse.groupstagesimulator.ui.teams

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.PlayersRepository
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.exceptions.NameEmptyException
import com.oceanscurse.groupstagesimulator.model.exceptions.TeamEmptyException
import com.oceanscurse.groupstagesimulator.model.Player
import com.oceanscurse.groupstagesimulator.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TeamDetailsViewModel : ViewModel() {

    /**
     * Cursor to keep track of which logo is currently selected.
     */
    private var mCurrentLogoIndex = 0

    /**
     * A list that will hold the players that will be assigned to the team.
     */
    private var mNewPlayers: MutableList<Player> = mutableListOf()

    /**
     * The name of the new team.
     */
    private var mNewTeamName: String = ""

    /**
     * The id of the team that will be created or updated.
     */
    private var mTeamId = 0

    private val _uiState = MutableStateFlow(TeamDetailsUiState())
    val uiState: StateFlow<TeamDetailsUiState> = _uiState.asStateFlow()

    /**
     * Sets the team id for the viewModel and gets the correct team data from the repository.
     */
    fun setTeamId(editingTeamId: Int) {
        val team = TeamsRepository.getTeamById(editingTeamId)
        mCurrentLogoIndex = Constants.LOGO_RESOURCE_IDS.indexOf(team?.logoResourceId).takeIf { it >= 0 } ?: 0
        mNewPlayers = team?.players?.toMutableList() ?: mutableListOf()
        mNewTeamName = team?.name ?: ""
        mTeamId = editingTeamId

        _uiState.update {
            it.copy(
                players = mNewPlayers,
                teamName = mNewTeamName,
                logoResourceId = Constants.LOGO_RESOURCE_IDS[mCurrentLogoIndex],
                isSaved = false
            )
        }
    }

    /**
     * Will select the previous logo in the list of logos the user can choose form.
     */
    fun selectPreviousLogo() {
        mCurrentLogoIndex--
        if (mCurrentLogoIndex < 0) {
            mCurrentLogoIndex = Constants.LOGO_RESOURCE_IDS.size - 1
        }
        updateLogo()
    }

    /**
     * Will select the next logo in the list of logos the user can choose form.
     */
    fun selectNextLogo() {
        mCurrentLogoIndex++
        if (mCurrentLogoIndex >= Constants.LOGO_RESOURCE_IDS.size) {
            mCurrentLogoIndex = 0
        }
        updateLogo()
    }

    /**
     * Updates the value for the team name, to make sure it persists when changing
     * other data in the uiState.
     */
    fun updateTeamName(newTeamName: String) {
        mNewTeamName = newTeamName
        _uiState.update { it.copy(teamName = newTeamName) }
    }

    /**
     * Will attempt to save the team to the repository.
     * Will only succeed if:
     * - Team name is not empty. => Will return NameEmptyException in uiState's saveException.
     * - Player count is 11. => Will return TeamEmptyException in uiState's saveException.
     */
    fun saveTeam() {
        val team = Team(mTeamId, mNewTeamName, Constants.LOGO_RESOURCE_IDS[mCurrentLogoIndex], mNewPlayers)

        if (team.name.isBlank()) {
            _uiState.update {
                it.copy(saveException = NameEmptyException())
            }
            return
        }

        if (team.players.size != 11) {
            _uiState.update {
                it.copy(saveException = TeamEmptyException())
            }
            return
        }

        TeamsRepository.updateTeam(team)
        _uiState.update { it.copy(isSaved = true) }
    }

    /**
     * Adds a set of 11 random players to the team.
     */
    fun randomizePlayers() {
        PlayersRepository.addRandomPlayersForTeam(mTeamId)
        mNewPlayers = PlayersRepository.getPlayersForTeam(mTeamId)
        _uiState.update {
            it.copy(players = mNewPlayers)
        }
    }

    /**
     * Will clear the exception, as the UI indicates it's handled.
     */
    fun notifyExceptionHandled() {
        _uiState.update {
            it.copy(saveException = null)
        }
    }

    /**
     * Will update the uiState with the current selected logo index.
     */
    private fun updateLogo() {
        _uiState.update {
            it.copy(logoResourceId = Constants.LOGO_RESOURCE_IDS[mCurrentLogoIndex])
        }
    }
}