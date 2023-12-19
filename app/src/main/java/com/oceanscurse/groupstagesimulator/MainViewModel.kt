package com.oceanscurse.groupstagesimulator

import androidx.lifecycle.ViewModel

/**
 * Created by Raymond de la Croix on 16-12-2023.
 *
 * This ViewModel is meant to aid in passing data between fragments.
 */
class MainViewModel : ViewModel() {
    /**
     * The id of the team that should be edited.
     */
    var editingTeamId = 0
}