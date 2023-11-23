package com.bhuwanupadhyay.mtts.infra.rest.resources.search

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound
import com.bhuwanupadhyay.mtts.infra.rest.resources.Fare
import com.bhuwanupadhyay.mtts.infra.rest.resources.Resource
import org.springframework.hateoas.RepresentationModel
import java.time.LocalDateTime

@Resource
data class SelectedSpaceTrain(val number: String,
                              val bound: Bound,
                              val originId: String,
                              val destinationId: String,
                              val departureSchedule: LocalDateTime,
                              val arrivalSchedule: LocalDateTime,
                              val fare: Fare) : RepresentationModel<SelectedSpaceTrain>()