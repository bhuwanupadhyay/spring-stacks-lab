package com.bhuwanupadhyay.mtts.infra.rest.resources.booking

import com.bhuwanupadhyay.mtts.infra.rest.resources.Fare
import com.bhuwanupadhyay.mtts.infra.rest.resources.Resource
import com.bhuwanupadhyay.mtts.infra.rest.resources.toResource
import java.time.LocalDateTime
import com.bhuwanupadhyay.mtts.domain.booking.SpaceTrain as DomainSpaceTrain


@Resource
data class SpaceTrain(val number: String,
                      val originId: String,
                      val destinationId: String,
                      val departureSchedule: LocalDateTime,
                      val arrivalSchedule: LocalDateTime,
                      val fare: Fare)

fun List<DomainSpaceTrain>.toResource(): List<SpaceTrain> = map { it.toResource() }

fun DomainSpaceTrain.toResource(): SpaceTrain = SpaceTrain(number, originId, destinationId, schedule.departure, schedule.arrival, fare.toResource())
