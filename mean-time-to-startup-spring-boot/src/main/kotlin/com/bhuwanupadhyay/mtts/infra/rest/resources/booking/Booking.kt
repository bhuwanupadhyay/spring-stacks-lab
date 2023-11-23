package com.bhuwanupadhyay.mtts.infra.rest.resources.booking

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Price
import com.bhuwanupadhyay.mtts.infra.rest.resources.Resource
import org.springframework.hateoas.RepresentationModel
import java.util.UUID

@Resource
class Booking(private val id: UUID, val spaceTrains: List<SpaceTrain>, val totalPrice: Price) : RepresentationModel<Booking>()