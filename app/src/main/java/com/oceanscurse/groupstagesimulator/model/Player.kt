package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 *
 * A player of a team.
 */
data class Player(

    /**
     * The name of the player.
     */
    val name: String,

    /**
     * The strength of the player.
     */
    val strength: Int,

    /**
     * The speed of the player.
     */
    val speed: Int,

    /**
     * The defence of the player.
     */
    val defence: Int,

    /**
     * The id of the team this player belongs to.
     */

    val teamId: Int
)
