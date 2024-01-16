package com.oceanscurse.groupstagesimulator.ui.simulator

import com.oceanscurse.groupstagesimulator.model.GroupStage
import com.oceanscurse.groupstagesimulator.model.hasTeamsAssigned

/**
 * Created by Raymond de la Croix on 16-12-2023.
 */
data class SimulatorUiState(

    /**
     * Whether or not the simulator has all the requirements set up to run.
     * In this case that means if there are at least the number of teams created
     * as defined in Constants.NUM_COMPETING_TEAMS.
     */
    val meetsRequirements: Boolean = false,
    /**
     * The groupStage containing the data from the simulation.
     */
    val groupStage: GroupStage
)


/**
 * Whether or not the user is allowed to press the assign teams button.
 */
val SimulatorUiState.allowAssigning: Boolean
    get() = groupStage.rounds.any { round -> !round.hasTeamsAssigned() }
            || groupStage.rounds.none { round -> round.isPlayed }


/**
 * Whether or not the user is allowed to press the play all button.
 */
val SimulatorUiState.allowPlayAll: Boolean
    get() = groupStage.rounds.all { round -> !round.isPlayed } && groupStage.rounds.all { round -> round.hasTeamsAssigned() }