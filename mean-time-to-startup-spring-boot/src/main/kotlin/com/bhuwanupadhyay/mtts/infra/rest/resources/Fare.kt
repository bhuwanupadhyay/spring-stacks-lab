package com.bhuwanupadhyay.mtts.infra.rest.resources

import com.bhuwanupadhyay.mtts.domain.sharedkernel.ComfortClass
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Price
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.LinkBuilder
import java.util.UUID
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Fare as DomainFare
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Fares as DomainFares

@Resource
data class Fare(private val id: UUID, val comfortClass: ComfortClass, val price: Price) : RepresentationModel<Fare>()

typealias Fares = Set<Fare>

fun DomainFares.toResource(spaceTrainLink: LinkBuilder): Fares = this.map { it.toResource(spaceTrainLink) }.toSet()

fun DomainFare.toResource(spaceTrainLink: LinkBuilder? = null): Fare {
    val fare = Fare(id, comfortClass, price)

    spaceTrainLink?.let {
        fare.add(spaceTrainLink.slash("fares")
                .slash("$id")
                .slash("select")
                .withRel("select"))
    }
    return fare
}