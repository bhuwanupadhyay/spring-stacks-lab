package com.bhuwanupadhyay.mtts.infra.rest.controllers

import com.bhuwanupadhyay.mtts.domain.api.RetrieveSpacePorts
import com.bhuwanupadhyay.mtts.domain.api.SearchForSpaceTrains
import com.bhuwanupadhyay.mtts.domain.api.SelectSpaceTrain
import com.bhuwanupadhyay.mtts.domain.api.`by resetting the selection`
import com.bhuwanupadhyay.mtts.domain.api.`in search`
import com.bhuwanupadhyay.mtts.domain.api.`with the fare`
import com.bhuwanupadhyay.mtts.domain.search.SpaceTrain.Companion.get
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound
import com.bhuwanupadhyay.mtts.domain.spaceport.SpacePort
import com.bhuwanupadhyay.mtts.domain.spi.Searches
import com.bhuwanupadhyay.mtts.infra.rest.resources.search.*
import com.bhuwanupadhyay.mtts.infra.rest.resources.Fare
import com.bhuwanupadhyay.mtts.infra.rest.resources.Fares
import com.bhuwanupadhyay.mtts.infra.rest.resources.search.*
import org.springframework.hateoas.IanaLinkRelations.SELF
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.LinkRelation.of
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.EntityLinks
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.hateoas.server.LinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.hateoas.server.mvc.add
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.time.LocalDateTime.parse
import java.util.UUID
import com.bhuwanupadhyay.mtts.domain.search.Search as DomainSearch
import com.bhuwanupadhyay.mtts.domain.search.SpaceTrain as DomainSpaceTrain
import com.bhuwanupadhyay.mtts.domain.search.SpaceTrains as DomainSpaceTrains
import com.bhuwanupadhyay.mtts.domain.search.criteria.Criteria as DomainCriteria
import com.bhuwanupadhyay.mtts.domain.search.criteria.Journey as DomainJourney
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Fare as DomainFare
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Fares as DomainFares


@RestController
@RequestMapping("/searches")
@ExposesResourceFor(Search::class)
class SearchController(
        private val `search for space trains`: SearchForSpaceTrains,
        private val `retrieve space ports`: RetrieveSpacePorts,
        private val `select space train`: SelectSpaceTrain,
        private val searches: Searches,
        private val entityLinks: EntityLinks
) {

    @PostMapping
    fun performASearch(@RequestBody criteria: Criteria): ResponseEntity<Search> {
        try {
            val domainCriteria = criteria.toDomainObject()
            val domainSearch = `search for space trains` satisfying domainCriteria
            val search = domainSearch.toResource()
            return created(search.getRequiredLink(SELF).toUri()).body(search)
        } catch (ex: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, ex.message, ex)
        }

    }

    @GetMapping("/{searchId}")
    fun retrieveAnExistingSearch(@PathVariable searchId: UUID): ResponseEntity<Search> {
        val domainSearch = retrieveSearch(searchId)
        return ok(domainSearch.toResource())
    }


    @GetMapping("/{searchId}/spacetrains")
    fun retrieveSpaceTrainsForBound(
            @PathVariable searchId: UUID,
            @RequestParam bound: Bound,
            @RequestParam onlySelectable: Boolean = false
    ): ResponseEntity<SpaceTrains> {
        val domainSearch = retrieveSearch(searchId)
        val searchLink = searchLink(searchId)
        val spaceTrain =
            when {
                onlySelectable -> domainSearch.selectableSpaceTrains(bound)
                else -> domainSearch.spaceTrains[bound]
            }

        val spaceTrains = SpaceTrains(spaceTrain.toResource(searchLink, !onlySelectable))
        spaceTrains
            .add(searchLink.withRel("search"))
            .add(searchLink.slash("selection").withRel("selection"))
            .linkToSpaceTrainsForBound(searchId, bound, SELF, onlySelectable)
        return ok(spaceTrains)
    }

    @PostMapping("/{searchId}/spacetrains/{spaceTrainNumber}/fares/{fareId}/select")
    fun selectSpaceTrainWithFare(
        @PathVariable searchId: UUID,
        @PathVariable spaceTrainNumber: String,
        @PathVariable fareId: UUID,
        @RequestParam resetSelection: Boolean = false
    ): ResponseEntity<Selection> {
        val search =
            `select space train` `having the number` spaceTrainNumber `with the fare` fareId `in search` searchId `by resetting the selection` resetSelection
        val selection = search.toSelectionResource()
        return ok(selection)
    }

    @GetMapping("/{searchId}/selection")
    fun selectSpaceTrainWithFare(@PathVariable searchId: UUID): ResponseEntity<Selection> {
        val search = retrieveSearch(searchId)
        val selection = search.toSelectionResource()
        return ok(selection)
    }

    private fun <R : RepresentationModel<R>> R.linkToSpaceTrainsForBound(
            searchId: UUID,
            bound: Bound,
            linkRelation: LinkRelation,
            onlySelectable: Boolean = false
    ): R {
        return this.add(SearchController::class) {
            linkTo { retrieveSpaceTrainsForBound(searchId, bound, onlySelectable) } withRel linkRelation
        }
    }

    private fun retrieveSearch(searchId: UUID) = (searches `find search identified by` searchId
        ?: throw ResponseStatusException(NOT_FOUND, "unknown search id $searchId"))

    private fun DomainSearch.toResource(): Search {
        val searchLink = searchLink(id)
        return Search(id, criteria.toResource())
            .add(searchLink.withSelfRel())
            .add(searchLink.slash("selection").withRel("selection"))
            .addIf(isSelectionComplete()) {
                linkTo(methodOn(BookingController::class.java).bookSomeSpaceTrainsFromTheSelectionOf(id)).withRel("create-booking")
            }
            .also { search ->
                spaceTrains.map { it.bound }.distinct()
                    .forEach { bound ->
                        search.linkToSpaceTrainsForBound(id, bound, of("all-${bound.toString().toLowerCase()}s"))
                        if (this.selection.hasASelectionFor(bound.oppositeWay())) {
                            search.linkToSpaceTrainsForBound(
                                id,
                                bound,
                                of("${bound.name.toLowerCase()}s-for-current-selection"),
                                onlySelectable = true
                            )
                        }
                    }
            }
    }

    private fun DomainSearch.toSelectionResource(): Selection {
        val searchLink = searchLink(id)
        val selectedSpaceTrain =
            selection.spaceTrains
                .map { selectedSpaceTrain ->
                    val spaceTrain = spaceTrains.first { it.number == selectedSpaceTrain.spaceTrainNumber }
                    SelectedSpaceTrain(
                        spaceTrain.number,
                        spaceTrain.bound,
                        spaceTrain.originId,
                        spaceTrain.destinationId,
                        spaceTrain.schedule.departure,
                        spaceTrain.schedule.arrival,
                        spaceTrain.fares.first { it.id == selectedSpaceTrain.fareId }.toResource()
                    )
                }
                .sortedBy { it.bound.ordinal }

        return Selection(selectedSpaceTrain, selection.totalPrice)
            .add(searchLink.withRel("search"))
            .add(searchLink.slash("selection").withSelfRel())
            .add(searchLink.slash("selection").withRel("selection"))
            .addIf(isSelectionComplete()) {
                linkTo(methodOn(BookingController::class.java).bookSomeSpaceTrainsFromTheSelectionOf(id)).withRel("create-booking")
            }
            .also { selection ->
                spaceTrains.map { it.bound }.distinct()
                    .asSequence()
                    .forEach { bound ->
                        selection.linkToSpaceTrainsForBound(id, bound, of("all-${bound.name.toLowerCase()}s"))
                        if (this.selection.hasASelectionFor(bound.oppositeWay())) {
                            selection.linkToSpaceTrainsForBound(
                                id,
                                bound,
                                of("${bound.name.toLowerCase()}s-for-current-selection"),
                                onlySelectable = true
                            )
                        }
                    }
            }
    }

    private fun searchLink(searchId: UUID) =
        entityLinks.linkForItemResource(Search::class.java, searchId)

    private fun Criteria.toDomainObject(): DomainCriteria =
        DomainCriteria(journeys.map {
            DomainJourney(
                it.departureSpacePortId.toString(),
                parse(it.departureSchedule),
                it.arrivalSpacePortId.toString()
            )
        })


    private fun URI.toDomainSpacePort(): SpacePort {
        val spacePortsUri = linkTo(SpacePortsController::class.java).toUri()
        val id = spacePortsUri.relativize(this).toString()
        return `retrieve space ports` `identified by` id
    }

    private fun DomainSpaceTrain.toResource(searchLink: LinkBuilder, resetSelection: Boolean): SpaceTrain = SpaceTrain(
        number,
        bound,
        originId,
        destinationId,
        schedule.departure,
        schedule.arrival,
        schedule.duration,
        fares.toResource(searchLink.slash("spacetrains").slash(number), resetSelection)
    )

    private fun DomainSpaceTrains.toResource(
        searchLink: LinkBuilder,
        resetSelection: Boolean = false
    ): List<SpaceTrain> = this.map { it.toResource(searchLink, resetSelection) }

    private fun DomainFares.toResource(spaceTrainLink: LinkBuilder, resetSelection: Boolean): Fares =
        this.map { it.toResource(spaceTrainLink, resetSelection) }.toSet()

    private fun DomainFare.toResource(spaceTrainLink: LinkBuilder? = null, resetSelection: Boolean = false): Fare {
        val fare = Fare(id, comfortClass, price)

        spaceTrainLink?.let {
            fare.add(
                spaceTrainLink.slash("fares")
                    .slash("$id")
                    .slash("select?resetSelection=$resetSelection")
                    .withRel("select")
            )
        }
        return fare
    }
}





