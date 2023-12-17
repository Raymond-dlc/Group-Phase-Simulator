package com.oceanscurse.groupstagesimulator.ui.simulator

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.GroupStage
import com.oceanscurse.groupstagesimulator.model.Match
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.random.Random

class SimulatorViewModel : ViewModel() {
    private var mGroupStage: GroupStage = GroupStage(0, mutableListOf(), listOf())

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

        teams.forEach {
            println("${it.name} ${it.totalTeamPoints()}")
        }

        mGroupStage.rounds.forEach { round ->
            teams.shuffle()
            round.matches.forEachIndexed { index, match ->
                match.homeTeam = teams[(index * 2) % Constants.NUM_COMPETING_TEAMS]
                match.awayTeam = teams[((index * 2) + 1) % Constants.NUM_COMPETING_TEAMS]
            }
        }
    }

    /**
     * Plays a round with the matching round it if it can be found. If it doesn't exist,
     * or the matches have no team assigned yet, it will return.
     *
     * Will loop through all matches in the round and calculate the score and assign them to
     * the match, also marks the round as played.
     */
    fun playRound(roundId: Int) {
        val round = mGroupStage.rounds.find { it.id == roundId } ?: return
        if (round.matches.any { it.homeTeam == null || it.awayTeam == null }) return

        round.let {
            it.matches.forEach { match ->
                val score = calculateScore(match.homeTeam!!, match.awayTeam!!)
                match.homeTeamScore = score.first
                match.awayTeamScore = score.second
            }
            round.isPlayed = true
        }

        _uiState.update {
            it.copy(groupStage = mGroupStage.copy())
        }
    }

    /**
     * Calculates the scores based on the team's total score with a little bit of advantage to the
     * home home.
     *
     * Score is calculate by the chances the team get to shoot at the goal, and the chance it has to
     * actually make a goal.
     *
     * The number of chances are decided by
     */
    fun calculateScore(homeTeam: Team, awayTeam: Team): Pair<Int, Int> {
        val homeTotalPoints = homeTeam.totalTeamPoints() + Constants.HOME_TEAM_ADVANTAGE_POINTS
        val awayTotalPoints = awayTeam.totalTeamPoints()
        val teamPointsDifference = abs(homeTotalPoints - awayTotalPoints)
        val homeShotsOnGoal: Int = ((homeTotalPoints / awayTotalPoints.toFloat()) * teamPointsDifference).toInt()
        val awayShotsOnGoal: Int = ((awayTotalPoints / homeTotalPoints.toFloat()) * teamPointsDifference).toInt()
        val homeScoreChance: Float = teamPointsDifference / awayTotalPoints.toFloat()
        val awayScoreChance: Float = teamPointsDifference / homeTotalPoints.toFloat()

        var homeTeamScore = 0;
        var awayTeamScore = 0;

        println("homeTeamTotalPoints $homeTotalPoints")
        println("awayTeamTotalPoints $awayTotalPoints")
        println("teamPointsDifference $teamPointsDifference")
        println("homeShotsOnGoal $homeShotsOnGoal")
        println("awayShotsOnGoal $awayShotsOnGoal")
        println("homeScoreChance $homeScoreChance")
        println("awayScoreChance $awayScoreChance")

        for (i in 0 until homeShotsOnGoal) {
            // Shot
            if (Random.nextFloat() <= homeScoreChance) {
                // Score!
                homeTeamScore++
            }
        }

        for (i in 0 until awayShotsOnGoal) {
            // Shot
            if (Random.nextFloat() <= awayScoreChance) {
                // Score!
                awayTeamScore++
            }
        }

        return Pair(homeTeamScore, awayTeamScore)
    }

}