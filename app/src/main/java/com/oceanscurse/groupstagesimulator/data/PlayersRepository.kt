package com.oceanscurse.groupstagesimulator.data

import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.model.Player
import kotlin.random.Random

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

        fun addRandomPlayersForTeam(teamId: Int) {
            val newPlayers = mutableListOf<Player>()
            val names = Constants.playerNames.shuffled()

            for (i in 0 until 11) {
                val strength = Random.nextInt(12, 20)
                val speed = Random.nextInt(12, 20)
                val defence = Random.nextInt(12, 20)
                val player = Player(names[i], strength, speed, defence, teamId)
                newPlayers.add(player)
            }
            clearPlayerForTeam(teamId)
            addPlayers(newPlayers)
        }
    }
}