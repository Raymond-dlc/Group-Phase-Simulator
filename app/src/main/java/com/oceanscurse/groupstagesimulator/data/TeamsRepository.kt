package com.oceanscurse.groupstagesimulator.data

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

        fun addTeam(team: Team) {
            mTeams.add(team)
        }
    }
}