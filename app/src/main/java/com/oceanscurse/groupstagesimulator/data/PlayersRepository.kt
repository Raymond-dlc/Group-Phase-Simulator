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

        /**
         * Gets a list of players for a specific team id.
         */
        fun getPlayersForTeam(teamId: Int): MutableList<Player> {
            return mPlayers
                .filter { it.teamId == teamId }
                .map { it }.toMutableList()
        }

        /**
         * Adds a list of players to the repository.
         */
        fun addPlayers(players: List<Player>) {
            mPlayers.addAll(players)
        }

        /**
         * Clears the list of playes for a specific team.
         */
        fun clearPlayerForTeam(teamId: Int) {
            mPlayers.removeIf { it.teamId == teamId }
        }

        /**
         * Generates a list of players with random names and random individual values and
         * stores that. The min and max values are defines in Constants.PLAYER_IV_MIN_VALUE and
         * Constants.PLAYER_IV_MAX_VALUE (+1 because nextInt is exclusive on the max).
         */
        fun addRandomPlayersForTeam(teamId: Int) {
            val newPlayers = mutableListOf<Player>()
            val names = Constants.PLAYER_NAMES.shuffled()

            for (i in 0 until 11) {
                val strength = Random.nextInt(Constants.PLAYER_IV_MIN_VALUE, Constants.PLAYER_IV_MAX_VALUE + 1)
                val speed = Random.nextInt(Constants.PLAYER_IV_MIN_VALUE, Constants.PLAYER_IV_MAX_VALUE + 1)
                val defence = Random.nextInt(Constants.PLAYER_IV_MIN_VALUE, Constants.PLAYER_IV_MAX_VALUE + 1)
                val player = Player(names[i], strength, speed, defence, teamId)
                newPlayers.add(player)
            }
            clearPlayerForTeam(teamId)
            addPlayers(newPlayers)
        }
    }
}