package com.bhuwanupadhyay.mtts.domain.search

import com.bhuwanupadhyay.mtts.domain.search.SpaceTrain.Companion.get
import com.bhuwanupadhyay.mtts.domain.search.criteria.Criteria
import com.bhuwanupadhyay.mtts.domain.search.criteria.Journeys
import com.bhuwanupadhyay.mtts.domain.search.selection.Selection
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound.INBOUND
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound.OUTBOUND
import com.bhuwanupadhyay.mtts.domain.sharedkernel.Bound.values
import java.util.UUID
import java.util.UUID.randomUUID

data class Search(
        val id: UUID = randomUUID(),
        val criteria: Criteria,
        val spaceTrains: SpaceTrains,
        val selection: Selection = Selection()
) {
    init {
        val (journeys) = criteria

        require(journeys.bounds `are all satified by a space train amongst` spaceTrains) {
            "some journeys don't have at least one corresponding space train"
        }

        require(spaceTrains `correspond to` journeys) {
            "some space trains don't correspond to any journey from the criteria"
        }

        require(selection `exists in` spaceTrains) {
            "unknown space train in the selection"
        }

        require(selection `with only known fares from` spaceTrains) {
            "unknown fare in the selection"
        }

        require(selection `corresponds to the bounds of` spaceTrains) {
            "selected space trains don't correspond to the right bound"
        }

        require(spaceTrainsDontHaveCompatibilitiesWhenOneWay()) {
            "SpaceTrains cannot have compatibilities in a one way search"
        }

        require(spaceTrainsHaveCompatibilitiesWhenRoundTrip()) {
            "SpaceTrains must have compatibilities in a round trip search"
        }

        require(spaceTrains.areNotCompatibleWithOtherSpaceTrainOnTheSameBound()) {
            "SpaceTrains cannot be compatible with another space train on the same bound"
        }

        require(spaceTrains.areCompatibleWithKnownOnes()) {
            "some SpaceTrains have unknown compatibilities"
        }

        require(spaceTrains.haveSymmetricCompatibilities()) {
            "some SpaceTrains don't respect a symmetric compatibility"
        }
    }


    fun getSpaceTrainWithNumber(wantedNumber: String): SpaceTrain = spaceTrains.first { it.number == wantedNumber }

    fun selectSpaceTrainWithFare(spaceTrainNumber: String, fareId: UUID, resetSelection: Boolean): Search {
        val spaceTrainToSelect = spaceTrains.first { it.number == spaceTrainNumber }

        val newSelection = getExistingSelectionOrReset(resetSelection, spaceTrainToSelect)

        val price = spaceTrainToSelect.fares.first { it.id == fareId }.price
        return copy(selection = newSelection.selectSpaceTrainWithFare(spaceTrainToSelect, fareId, price))
    }

    fun isSelectionComplete(): Boolean {
        return selection.bounds.size == criteria.journeys.size
    }

    fun selectableSpaceTrains(bound: Bound): List<SpaceTrain> {
        return when {
            selection.isEmpty() -> spaceTrains[bound]

            else -> selection.spaceTrainsByBound
                .asSequence()
                .filter { it.key != bound }
                .map { getSpaceTrainWithNumber(it.value.spaceTrainNumber) }
                .map { it.compatibleSpaceTrains }
                .flatten()
                .map { getSpaceTrainWithNumber(it) }
                .filter { it.bound == bound }
                .toList()
        }
    }

    private fun spaceTrainsHaveCompatibilitiesWhenRoundTrip(): Boolean {
        if (!criteria.isOneWay()) {
            return spaceTrains.all { it.compatibleSpaceTrains.isNotEmpty() }
        }
        return true
    }

    private fun spaceTrainsDontHaveCompatibilitiesWhenOneWay(): Boolean {
        if (criteria.isOneWay()) {
            return spaceTrains.all { it.compatibleSpaceTrains.isEmpty() }
        }
        return true
    }

    private fun getExistingSelectionOrReset(resetSelection: Boolean, spaceTrainToSelect: SpaceTrain): Selection {
        return when {
            resetSelection -> {
                Selection()
            }

            else -> {
                require(spaceTrainToSelect.isCompatibleWithSelection()) {
                    "cannot select incompatible space trains"
                }
                selection
            }
        }
    }


    private fun SpaceTrain.isCompatibleWithSelection(): Boolean {
        val alreadySelectedBounds = selection.bounds.filter { it != bound }
        return alreadySelectedBounds.map { selection[it] }
            .all { selectedSpaceTrain ->
                spaceTrains.first { it.number == selectedSpaceTrain?.spaceTrainNumber }
                    .compatibleSpaceTrains.contains(number)
            }
    }

    private infix fun Selection.`exists in`(spaceTrainsFromSearch: SpaceTrains): Boolean =
        spaceTrainsFromSearch.map { it.number }.containsAll(spaceTrains.map { it.spaceTrainNumber })

    private infix fun Selection.`with only known fares from`(spaceTrainsFromSearch: SpaceTrains): Boolean =
        spaceTrains.all { selectedSpaceTrain ->
            spaceTrainsFromSearch.find { it.number == selectedSpaceTrain.spaceTrainNumber }?.fares?.any { it.id == selectedSpaceTrain.fareId }
                ?: false
        }

    private infix fun Selection.`corresponds to the bounds of`(spaceTrainsFromSearch: SpaceTrains): Boolean =
        all { spaceTrainsFromSearch.find { spaceTrain -> spaceTrain.number == it.value.spaceTrainNumber }?.bound == it.key }

    private val Journeys.bounds
        get(): List<Bound> = indices.map { values()[it] }

    private infix fun List<Bound>.`are all satified by a space train amongst`(spaceTrains: SpaceTrains): Boolean {
        return spaceTrains.isEmpty() || spaceTrains.map { it.bound }.containsAll(this)
    }

    private infix fun SpaceTrains.`correspond to`(journeys: Journeys): Boolean {
        return all { it `corresponds to` journeys }
    }

    private infix fun SpaceTrain.`corresponds to`(journeys: Journeys): Boolean {
        return journeys.any { it.departureSpacePortId == originId && it.arrivalSpacePortId == destinationId }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Search

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}

private fun List<SpaceTrain>.haveSymmetricCompatibilities(): Boolean {
    return all { spaceTrain ->
        spaceTrain.compatibleSpaceTrains
            .all { compatibleNumber -> first { it.number == compatibleNumber }.compatibleSpaceTrains.contains(spaceTrain.number) }
    }

}

private fun List<SpaceTrain>.areCompatibleWithKnownOnes(): Boolean {
    val spaceTrainByBound = groupBy { it.bound }

    val outboundSpaceTrains = spaceTrainByBound[OUTBOUND] ?: return true
    val outboundSpaceTrainsCompatibilities = outboundSpaceTrains.map {
        it.compatibleSpaceTrains
    }.flatten()

    val inboundSpaceTrains = spaceTrainByBound[INBOUND] ?: return true
    val inboundSpaceTrainCompatibilities = inboundSpaceTrains.map { it.compatibleSpaceTrains }.flatten()

    return outboundSpaceTrainsCompatibilities.all { outboundCompatibility -> inboundSpaceTrains.any { outboundCompatibility == it.number } }
        .and(inboundSpaceTrainCompatibilities.all { inboundCompatibility -> outboundSpaceTrains.any { inboundCompatibility == it.number } })
}

private fun Criteria.isOneWay(): Boolean = journeys.size == 1

private fun List<SpaceTrain>.areNotCompatibleWithOtherSpaceTrainOnTheSameBound(): Boolean {
    return groupBy { it.bound }.values
        .all { spaceTrainsOnTheSameBound ->
            spaceTrainsOnTheSameBound
                .map { it.compatibleSpaceTrains }
                .flatten()
                .none { aCompatibleSpaceTrainNumber -> spaceTrainsOnTheSameBound.any { it.number == aCompatibleSpaceTrainNumber } }
        }
}
