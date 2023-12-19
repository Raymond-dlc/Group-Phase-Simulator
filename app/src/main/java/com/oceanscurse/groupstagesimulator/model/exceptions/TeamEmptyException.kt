package com.oceanscurse.groupstagesimulator.model.exceptions

/**
 * Created by Raymond de la Croix on 16-12-2023.
 *
 * An Exception that should be thrown when anything is attempted that should require
 * a full team, but does not have a full team ready.
 */
class TeamEmptyException : Exception()