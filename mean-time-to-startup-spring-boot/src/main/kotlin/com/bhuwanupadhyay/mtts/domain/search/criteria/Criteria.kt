package com.bhuwanupadhyay.mtts.domain.search.criteria

import java.time.LocalDateTime

data class Criteria(val journeys: Journeys) {
    init {
        require(journeys.isNotEmpty())
        { "Criteria must contain at least one journey" }

        require(journeys.areOrderedByDepartureSchedule())
        { "Criteria must only have journeys ordered by departure schedule" }

        require(journeys.areConnectedTogether())
        { "Criteria must only have connected journeys" }

        require(journeys.haveAtLeastADayBetweenEach()) {
            "An elapse time of 5 days must be respected between journeys"
        }
    }

    private fun Journeys.areConnectedTogether() =
            this.zipWithNext { journey, nextJourney -> journey `is connected to` nextJourney }.all { it }

    private fun Journeys.areOrderedByDepartureSchedule() =
            this.sortedBy { it.departureSchedule } == this

    private fun Journeys.haveAtLeastADayBetweenEach(): Boolean = map { it.departureSchedule }
            .zipWithNext { departureOfJourney, departureOfNextJourney -> departureOfNextJourney `is at least 5 days after` departureOfJourney }
            .all { it }

    private infix fun LocalDateTime.`is at least 5 days after`(departureOfPreviousJourney: LocalDateTime): Boolean =
            coerceAtLeast(departureOfPreviousJourney.plusDays(5)) == this
}