package com.bhuwanupadhyay.mtts.domain.spi

import com.bhuwanupadhyay.mtts.domain.booking.Booking
import java.util.UUID

interface Bookings {
    infix fun `find booking identified by`(bookingId: UUID): Booking?
    infix fun save(booking: Booking): Booking
}