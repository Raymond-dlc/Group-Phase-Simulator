package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * The entire groupStage that can be simulated.
 * It contains a list of rounds to be played, and keeps tracks of the results
 * that belong to those rounds.
 */
class GroupStage(

    /**
     * The id of the GroupStage.
     */
    val id: Int,

    /**
     * The rounds that are to be played in this GroupStage.
     */
    var rounds: MutableList<Round>,

    /**
     * The results of the rounds and matches played in this GroupStage.
     */
    var results: MutableList<Result>
) {

    /**
     * Copies the GroupStage to a new Instance.
     */
    fun copy(): GroupStage {
        return GroupStage(
            id, rounds, results
        )
    }
}
