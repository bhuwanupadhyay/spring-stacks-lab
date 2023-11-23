package com.bhuwanupadhyay.mtts.domain.api

import com.bhuwanupadhyay.mtts.domain.search.Search
import java.util.UUID

interface SelectSpaceTrain {
    infix fun `having the number`(spaceTrainNumber: String): SelectFare = selectSpaceTrain(spaceTrainNumber)
    fun selectFareOfSpaceTrainInSearch(spaceTrainNumber: String, fareId: UUID, searchId: UUID, resetSelection: Boolean): Search

    private fun selectSpaceTrain(number: String): SelectFare =
            { fareId: UUID -> this.selectFare(number, fareId) }

    private fun selectFare(number: String, fareId: UUID): InSearchSelection =
            { searchId: UUID -> this.byResettingSelection(number, fareId, searchId) }


    private fun byResettingSelection(number: String, fareId: UUID, searchId: UUID): ByResettingSelection =
            { resetSelection: Boolean ->
                this.selectFareOfSpaceTrainInSearch(number, fareId, searchId, resetSelection)
            }
}

typealias SelectFare = (UUID) -> InSearchSelection

infix fun SelectFare.`with the fare`(fareId: UUID): InSearchSelection = this.invoke(fareId)

typealias InSearchSelection = (UUID) -> ByResettingSelection

infix fun InSearchSelection.`in search`(searchId: UUID): ByResettingSelection = this.invoke(searchId)

typealias ByResettingSelection = (Boolean) -> Search

infix fun ByResettingSelection.`by resetting the selection`(resetSelection: Boolean): Search = this.invoke(resetSelection)