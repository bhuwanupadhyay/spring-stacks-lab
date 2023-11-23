package com.bhuwanupadhyay.mtts.domain.sharedkernel

enum class Bound {
    OUTBOUND,
    INBOUND;

    companion object {
        fun fromJourneyIndex(journeyIndex: Int): Bound {
            val bounds = values()
            val maxIndex = bounds.size

            require(journeyIndex in 0 until maxIndex) {
                "Journey index $journeyIndex is outside the supported range [0,${maxIndex.dec()}]"
            }
            return bounds[journeyIndex]
        }
    }

    fun oppositeWay(): Bound = fromJourneyIndex((ordinal + 1) % 2)
}