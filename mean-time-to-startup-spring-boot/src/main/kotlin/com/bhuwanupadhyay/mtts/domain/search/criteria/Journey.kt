package com.bhuwanupadhyay.mtts.domain.search.criteria

import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class Journey(val departureSpacePortId: String,
                   val departureSchedule: LocalDateTime,
                   val arrivalSpacePortId: String) {
    init {
        require(departureSchedule.isAfter(now()))
        { "Cannot create a Journey with a departure scheduled in the past" }
    }

    infix fun `is connected to`(nextJourney: Journey): Boolean {
        return arrivalSpacePortId == nextJourney.departureSpacePortId
    }
}

typealias Journeys = List<Journey>