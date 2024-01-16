package com.oceanscurse.groupstagesimulator.ui.simulator

import androidx.lifecycle.ViewModel
import com.oceanscurse.groupstagesimulator.Constants
import com.oceanscurse.groupstagesimulator.data.TeamsRepository
import com.oceanscurse.groupstagesimulator.model.GroupStage
import com.oceanscurse.groupstagesimulator.model.Match
import com.oceanscurse.groupstagesimulator.model.Result
import com.oceanscurse.groupstagesimulator.model.Round
import com.oceanscurse.groupstagesimulator.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class SimulatorViewModel : ViewModel() {
    private var mGroupStage: GroupStage = GroupStage(0, mutableListOf(), mutableListOf())

    private val _uiState = MutableStateFlow(SimulatorUiState(groupStage = mGroupStage))
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    /**
     * Checks if the simulator meets the conditions to start. For now this only means if there's
     * enough teams created.
     */
    fun checkConditions() {
        val meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS

        // If we didn't meet the requirement before, and now we do, reset to start a clean group stage.
        if (!_uiState.value.meetsRequirements && meetsRequirements) {
            reset()
        }

        _uiState.update {
            it.copy(
                groupStage = mGroupStage,
                meetsRequirements = meetsRequirements
            )
        }
    }

    /**
     * Assigns the teams to the rounds, preparing them for the play-offs.
     * Does so by shuffling the teams, and assigning them
     */
    fun assignTeams() {
        val teams = TeamsRepository.getTeams()
        val pairs = mutableListOf<Pair<Team, Team>>()

        // Form pairs
        for (i in 0 until teams.size - 1) {
            for (j in i + 1 until teams.size) {
                pairs.add(Pair(teams[i], teams[j]))
            }
        }

        // Some randomness to not have the same teams have home field advantage always.
        pairs.forEachIndexed { index, pair ->
            // 50% chance to swap the sides
            if (Math.random() > 0.5f) {
                pairs[index] = pair.copy(pair.second, pair.first)
            }
        }

        // Assign the pairs to the matches
        mGroupStage.rounds.forEach { round ->
            round.matches.forEach { match ->
                val assignedPairs = mutableListOf<Pair<Team, Team>>()
                pairs.forEach { pair ->
                    if (match.homeTeam == null) {
                        val teamAlreadyPlaysInTheRound = round.matches.any {
                            it.homeTeam == pair.first || it.awayTeam == pair.first || it.homeTeam == pair.second || it.awayTeam == pair.second
                        }
                        if (!teamAlreadyPlaysInTheRound) {
                            match.homeTeam = pair.first
                            match.awayTeam = pair.second
                            assignedPairs.add(pair)
                        }
                    }
                }
                pairs.removeAll(assignedPairs)
                assignedPairs.clear()
            }
        }

        // Clear the results and add the new teams.
        mGroupStage.results.clear()
        teams.forEachIndexed { index, team ->
            mGroupStage.results.add(Result(index + 1, team))
        }

        _uiState.update {
            it.copy(
                groupStage = mGroupStage.copy(),
            )
        }
    }

    /**
     * Resets the simulation by creating a new groupStage with empty rounds and results.
     * After that, the UiState will be updated with the empty GroupStage.
     */
    fun reset() {
        val meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS
        mGroupStage = GroupStage(0, mutableListOf(), mutableListOf())

        if (meetsRequirements) {
            if (mGroupStage.rounds.isEmpty()) {
                generateRounds()
            }
        }

        _uiState.update {
            it.copy(
                groupStage = mGroupStage.copy(),
                meetsRequirements = TeamsRepository.getTeams().size >= Constants.NUM_COMPETING_TEAMS
            )
        }
    }

    /**
     * Will automatically play each round that's in the group stage.
     */
    fun playEntireGroupStage() {
        mGroupStage.rounds.forEach {
            playRound(it.id)
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
            it.matches.forEach { match -> playMatch(match) }
            round.isPlayed = true
        }

        updateStandings(round)
        sortStandings()

        _uiState.update {
            it.copy(groupStage = mGroupStage.copy())
        }
    }

    /**
     * Will play out a match, calculating the scores for both teams.
     */
    fun playMatch(match: Match) {
        val score = calculateScore(match.homeTeam!!, match.awayTeam!!)
        match.homeTeamScore = score.first
        match.awayTeamScore = score.second
    }

    /**
     * Calculates the scores based on the team's total score with a little bit of advantage to the
     * home home.
     *
     * Score is calculate by the chances the team get to shoot at the goal, and the chance it has to
     * actually make a goal.
     *
     * The number of chances are decided by comparing the total strengths and the difference. The bigger
     * difference in score, the more chances the stronger team will have.
     *
     * For each shot on the goal, there is a chance for it to actually score. This depends on the difference
     * in score and the total score.
     */
    fun calculateScore(homeTeam: Team, awayTeam: Team): Pair<Int, Int> {
        val homeTotalPoints = homeTeam.totalTeamPoints() + Constants.HOME_TEAM_ADVANTAGE_POINTS
        val awayTotalPoints = awayTeam.totalTeamPoints()

        val strongTeamChances = (max(homeTotalPoints, awayTotalPoints) / min(homeTotalPoints, awayTotalPoints).toDouble())
        val weakTeamChances = 1 - (strongTeamChances - 1)

        val homeMultiplier = if (homeTotalPoints > awayTotalPoints) strongTeamChances else weakTeamChances
        val awayMultiplier = if (awayTotalPoints > homeTotalPoints) strongTeamChances else weakTeamChances

        val homeShotsOnGoal: Int = (15 * (homeMultiplier).pow(5)).toInt()
        val awayShotsOnGoal: Int = (15 * (awayMultiplier).pow(5)).toInt()

        val homeScoreChance: Double = 0.1 * (homeMultiplier).pow(10)
        val awayScoreChance: Double = 0.1 * (awayMultiplier).pow(10)

        var homeTeamScore = 0
        var awayTeamScore = 0

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

    /**
     * Updates the standings depending on the played round's result.
     * Winner gets 2 points.
     * Tiebreakers give both teams 1 point.
     * Loser gets 0 points.
     *
     * Increments the times played for both teams.
     * Adds to goals for and against, and updates the net score.
     */
    private fun updateStandings(playedRound: Round) {
        playedRound.matches.forEach { match ->
            val homeTeamResults = mGroupStage.results.find { it.team.id == match.homeTeam?.id }
            val awayTeamResults = mGroupStage.results.find { it.team.id == match.awayTeam?.id }

            homeTeamResults?.apply {
                timesPlayed++
                if (match.homeTeamScore > match.awayTeamScore) {
                    wins++
                    points += 2
                } else if (match.homeTeamScore == match.awayTeamScore) {
                    draws++
                    points += 1
                } else {
                    losses++
                }
                goals += match.homeTeamScore
                goalsAgainst += match.awayTeamScore
                netScore = goals - goalsAgainst
            }

            awayTeamResults?.apply {
                timesPlayed++
                if (match.awayTeamScore > match.homeTeamScore) {
                    wins++
                    points += 2
                } else if (match.awayTeamScore == match.homeTeamScore) {
                    draws++
                    points += 1
                } else {
                    losses++
                }
                goals += match.awayTeamScore
                goalsAgainst += match.homeTeamScore
                netScore = goals - goalsAgainst
            }
        }
    }

    /**
     * Will sort the standings based on the following priority:
     * 1. Points
     * 2. Goal difference
     * 3. Goals for
     * 4. Goals against
     * 5. Head 2 head result
     */
    private fun sortStandings() {
        mGroupStage.results.sortWith(Comparator { resultA, resultB ->
            // points
            if (resultA.points > resultB.points) return@Comparator -1
            if (resultB.points > resultA.points) return@Comparator 1

            // Goal difference
            if (resultA.netScore > resultB.netScore) return@Comparator -1
            if (resultB.netScore > resultA.netScore) return@Comparator 1

            // Goals for
            if (resultA.goals > resultB.goals) return@Comparator -1
            if (resultB.goals > resultA.goals) return@Comparator 1

            // Goals against
            if (resultA.goalsAgainst < resultB.goalsAgainst) return@Comparator -1
            if (resultB.goalsAgainst < resultA.goalsAgainst) return@Comparator 1

            // Head 2 head
            var winsA = 0
            var winsB = 0
            mGroupStage.rounds.forEach { round ->
                round.matches.forEach { match ->
                    if ((match.homeTeam?.id == resultA.team.id && match.awayTeam?.id == resultB.team.id)) {
                        if (match.homeTeamScore > match.awayTeamScore) {
                            winsA++
                        } else if (match.homeTeamScore < match.awayTeamScore) {
                            winsB++
                        }
                    }
                }
            }
            if (winsA > winsB) return@Comparator -1
            if (winsB > winsA) return@Comparator 1
            return@Comparator 0
        })

        mGroupStage.results.forEachIndexed { index, result ->
            result.position = index + 1
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

}