package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class GroupStage(
    val id: Int,
    var rounds: List<Round>,
    var results: List<Result>
)
