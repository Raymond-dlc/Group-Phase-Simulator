package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
class GroupStage(
    val id: Int,
    var rounds: MutableList<Round>,
    var results: MutableList<Result>
) {
    fun copy(): GroupStage {
        return GroupStage(
            id, rounds, results
        )
    }
}
