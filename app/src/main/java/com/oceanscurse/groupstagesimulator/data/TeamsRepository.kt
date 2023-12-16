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

        // Returns a list if all stored teams.
        fun getTeams(): MutableList<Team> {
            return mTeams.map { it }.toMutableList()
        }

        // Find the team by id or null.
        fun getTeamById(teamId: Int): Team? {
            return mTeams.find { it.id == teamId }
        }

        // Updates a team but removing it if found and then store it
        fun updateTeam(team: Team) {
            mTeams.removeIf { it.id == team.id }
            mTeams.add(team)
        }

        // Stores the supplied team
        fun addTeam(team: Team) {
            mTeams.add(team)
        }

        // Creates a random team with the name "Team + {teamId+1}}
        // Logo is taken randomly (causing it to sometimes create duplicate logos)
        // Players are generated with random names and stats.
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