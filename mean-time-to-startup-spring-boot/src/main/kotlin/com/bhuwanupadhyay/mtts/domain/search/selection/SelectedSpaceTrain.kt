package com.bhuwanupadhyay.mtts.domain.search.selection

import com.bhuwanupadhyay.mtts.domain.sharedkernel.Price
import java.util.UUID

data class SelectedSpaceTrain(val spaceTrainNumber: String, val fareId: UUID, val price: Price)