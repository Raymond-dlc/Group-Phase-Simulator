package com.oceanscurse.groupstagesimulator.ui.simulator

import com.oceanscurse.groupstagesimulator.model.GroupStage
import com.oceanscurse.groupstagesimulator.model.Round

/**
 * Created by Raymond de la Croix on 16-12-2023.
 */
data class SimulatorUiState(
    val meetsRequirements: Boolean = false,
    val groupStage: GroupStage? = null
)
