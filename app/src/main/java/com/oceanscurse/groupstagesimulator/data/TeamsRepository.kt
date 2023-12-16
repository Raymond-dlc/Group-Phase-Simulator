package com.oceanscurse.groupstagesimulator.data

import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.model.Player
import com.oceanscurse.groupstagesimulator.model.Team

/**
 * Created by Raymond de la Croix on 14/12/2023.
 */
class TeamsRepository {
    companion object {
        private var mTeams = mutableListOf<Team>()

        fun getTeams(): MutableList<Team> {
            return mTeams.map { it }.toMutableList()
        }

        fun getTeamById(teamId: Int): Team? {
            return mTeams.find { it.id == teamId }
        }

        fun updateTeam(team: Team) {
            mTeams.removeIf { it.id == team.id }
            mTeams.add(team)
        }

        fun addTeam(team: Team) {
            mTeams.add(team)
        }

        fun createFullTeam(teamId: Int) {
            PlayersRepository.addRandomPlayersForTeam(teamId)
            val team = Team(
                teamId,
                "Team ${teamId + 1}",
                Constants.logosResourceIds.random(),
                PlayersRepository.getPlayersForTeam(teamId)
            )
            updateTeam(team)
        }
    }
}