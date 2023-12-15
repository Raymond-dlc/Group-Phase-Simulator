package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class GroupStage(
    val id: Int,
    val rounds: List<Round>,
    val results: List<Result>
)
