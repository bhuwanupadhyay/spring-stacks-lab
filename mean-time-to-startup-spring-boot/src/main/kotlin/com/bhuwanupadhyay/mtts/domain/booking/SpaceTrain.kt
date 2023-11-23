package com.bhuwanupadhyay.mtts.domain.booking

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Fare
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Schedule

data class SpaceTrain(val number: String,
                      val originId: String,
                      val destinationId: String,
                      val schedule: Schedule,
                      val fare: Fare)