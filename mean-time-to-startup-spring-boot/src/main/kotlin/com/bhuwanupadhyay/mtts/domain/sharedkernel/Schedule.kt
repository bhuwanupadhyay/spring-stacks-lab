package com.bhuwanupadhyay.mtts.domain.sharedkernel

import java.time.Duration
import java.time.Duration.between
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class Schedule(val departure: LocalDateTime, val arrival: LocalDateTime) {
    val duration: Duration = between(departure, arrival)

    init {
        require(departure.isAfter(now())) {
            "departure cannot be in the past"
        }

        require(arrival.isAfter(departure)) {
            "arrival cannot precede the departure"
        }
    }
}