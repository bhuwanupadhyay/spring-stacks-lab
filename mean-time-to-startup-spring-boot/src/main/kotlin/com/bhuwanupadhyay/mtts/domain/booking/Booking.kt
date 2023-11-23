package com.bhuwanupadhyay.mtts.domain.booking

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Price
import java.util.UUID
import java.util.UUID.randomUUID


data class Booking(val id: UUID = randomUUID(), val spaceTrains: List<SpaceTrain>) {

    val totalPrice: Price get() = spaceTrains.map { it.fare.price }.reduce(Price::plus)

    init {
        require(spaceTrains.isNotEmpty()) {
            "cannot book nothing"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Booking

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}