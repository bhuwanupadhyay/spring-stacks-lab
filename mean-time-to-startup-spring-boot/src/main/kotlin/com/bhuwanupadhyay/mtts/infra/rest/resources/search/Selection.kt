package com.bhuwanupadhyay.mtts.infra.rest.resources.search

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Price
import com.bhuwanupadhyay.mtts.infra.rest.resources.Resource
import org.springframework.hateoas.RepresentationModel


@Resource
data class Selection(val spaceTrains: List<SelectedSpaceTrain>, val totalPrice: Price?) : RepresentationModel<Selection>()