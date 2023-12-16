package com.oceanscurse.groupstagesimulator.model

/**
 * Created by Raymond de la Croix on 13/12/2023.
 */
data class Player(
    val name: String,
    val strength: Int,
    val speed: Int,
    val defence: Int,
    val teamId: Int
)
