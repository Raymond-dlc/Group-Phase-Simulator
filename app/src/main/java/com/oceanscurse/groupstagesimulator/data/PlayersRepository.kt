package com.oceanscurse.groupstagesimulator.data

import com.oceanscurse.groupstagesimulator.model.Player

/**
 * Created by Raymond de la Croix on 14/12/2023.
 *
 * This repository is responsible for everything related to the players.
 */
class PlayersRepository {
    companion object {
        private var mPlayers = mutableListOf<Player>()

        fun getPlayersForTeam(teamId: Int): MutableList<Player> {
            return mPlayers
                .filter { it.teamId == teamId }
                .map { it }.toMutableList()
        }

        fun addPlayers(players: List<Player>) {
            mPlayers.addAll(players)
        }

        fun clearPlayerForTeam(teamId: Int) {
            mPlayers.removeIf { it.teamId == teamId }
        }
    }
}