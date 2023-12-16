package com.oceanscurse.groupstagesimulator.ui.teams

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.PlayersRepository
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.Exceptions.NameEmptyException
import com.oceanscurse.groupstagesimulator.model.Exceptions.TeamEmptyException
import com.oceanscurse.groupstagesimulator.model.Player
import com.oceanscurse.groupstagesimulator.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

// TODO: Add team id.
class TeamDetailsViewModel : ViewModel() {

    private var mCurrentLogoIndex = 0
    private var mNewPlayers: MutableList<Player> = mutableListOf()
    private var mNewTeamName: String = ""
    private var mTeamId = 0

    private val _uiState = MutableStateFlow(TeamDetailsUiState())
    val uiState: StateFlow<TeamDetailsUiState> = _uiState.asStateFlow()

    // Sets the team id for the viewModel and gets the correct team data from the
    // repository
    fun setTeamId(editingTeamId: Int) {
        println("we would like to see team : $editingTeamId")
        val team = TeamsRepository.getTeamById(editingTeamId)
        println(team)
        mCurrentLogoIndex = Constants.logosResourceIds.indexOf(team?.logoResourceId).takeIf { it >= 0 } ?: 0
        mNewPlayers = team?.players?.toMutableList() ?: mutableListOf()
        mNewTeamName = team?.name ?: ""
        mTeamId = editingTeamId

        println("new players $mNewPlayers")

        _uiState.update {
            it.copy(
                players = mNewPlayers,
                teamName = mNewTeamName,
                logoResourceId = Constants.logosResourceIds[mCurrentLogoIndex],
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
            mCurrentLogoIndex = Constants.logosResourceIds.size - 1
        }
        updateLogo()
    }

    /**
     * Will select the next logo in the list of logos the user can choose form.
     */
    fun selectNextLogo() {
        mCurrentLogoIndex++
        if (mCurrentLogoIndex >= Constants.logosResourceIds.size) {
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
     * - Team name is not empty. => Will return NameEmptyException in uiState's saveException
     * - Player count is 11. => Will return TeamEmptyException in uiState's saveException
     */
    fun saveTeam() {
        val team = Team(mTeamId, mNewTeamName, Constants.logosResourceIds[mCurrentLogoIndex], mNewPlayers)

        if (team.name.isBlank()) {
            _uiState.update {
                it.copy(saveException = NameEmptyException())
            }
            return
        }

        println("size: ${team.players.size}")
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
        mNewPlayers = mutableListOf<Player>()
        val names = Constants.playerNames.shuffled()

        for (i in 0 until 11) {
            val strength = Random.nextInt(12, 20)
            val speed = Random.nextInt(12, 20)
            val defence = Random.nextInt(12, 20)
            val player = Player(names[i], strength, speed, defence, mTeamId)
            mNewPlayers.add(player)
        }
        PlayersRepository.clearPlayerForTeam(mTeamId)
        PlayersRepository.addPlayers(mNewPlayers)

        _uiState.update {
            it.copy(players = PlayersRepository.getPlayersForTeam(mTeamId))
        }
    }

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
            it.copy(logoResourceId = Constants.logosResourceIds[mCurrentLogoIndex])
        }
    }
}