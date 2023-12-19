package com.oceanscurse.groupstagesimulator

/**
 * Created by Raymond de la Croix on 16-12-2023.
 *
 * App wide constants.
 */
object Constants {

    /**
     * The number of teams competing in the simulation.
     */
    const val NUM_COMPETING_TEAMS = 4

    /**
     * The number of rounds to be played in the simulation.
     */
    const val NUM_ROUNDS = 3

    /**
     * The number of matches that are to be played in each round.
     */
    const val NUM_MATCHES_PER_ROUND = 2

    /**
     * The amount of points a team will get as a bonus when playing a home game.
     */
    const val HOME_TEAM_ADVANTAGE_POINTS = 3

    /**
     * The minimum value for an individual value for a player.
     */
    const val PLAYER_IV_MIN_VALUE = 12

    /**
     * The maximum value for an individual value for a player.
     */
    const val PLAYER_IV_MAX_VALUE = 22

    /**
     * A list of logo images that can be used to fill the teams.
     */
    val LOGO_RESOURCE_IDS = mutableListOf(
        R.drawable.logo_1,
        R.drawable.logo_2,
        R.drawable.logo_3,
        R.drawable.logo_4,
        R.drawable.logo_5,
        R.drawable.logo_6,
        R.drawable.logo_7,
        R.drawable.logo_8,
        R.drawable.logo_9
    )

    /**
     * A list of random names that can be used to fill the teams.
     */
    val PLAYER_NAMES = mutableListOf(
        "Broderick Argenbright",
        "Elton Eklov",
        "Emmet Faeth",
        "Lucio Funt",
        "Sol Siefferman",
        "Abhinav Fazzone",
        "Desean Gentry",
        "Isa Kaber",
        "Josh Venditti",
        "Paris Wylam",
        "Ahmed Barbara",
        "Bjorn Gremo",
        "Javier Millberg",
        "Rio Ordner",
        "Smith Siefferman",
        "Alberto Budiao",
        "Hailey Busman",
        "June Conners",
        "Nicholas Crofton",
        "Patsy Griep",
        "Jett Clunie",
        "Quinten Freedle",
        "Tavion Horvat",
        "Tyrese Mcclallen",
        "Yusuf Thompkins",
        "Canaan Bakowski",
        "Hadi Caris",
        "Rashad Clusky",
        "Ty Tangert",
        "Wilder Veto",
        "Azariah Coutts",
        "Gino Rhude",
        "Holland Sirnio",
        "Kylen Tempesta",
        "Leilani Wahlquist"
    )
}