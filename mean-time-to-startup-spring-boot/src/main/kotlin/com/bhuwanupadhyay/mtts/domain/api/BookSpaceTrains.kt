package com.bhuwanupadhyay.mtts.domain.api

import com.bhuwanupadhyay.mtts.domain.booking.Booking
import com.bhuwanupadhyay.mtts.domain.search.Search
import com.bhuwanupadhyay.mtts.domain.spi.Bookings

interface BookSpaceTrains {
    val bookings: Bookings
    infix fun `from the selection of`(search: Search): Booking
}