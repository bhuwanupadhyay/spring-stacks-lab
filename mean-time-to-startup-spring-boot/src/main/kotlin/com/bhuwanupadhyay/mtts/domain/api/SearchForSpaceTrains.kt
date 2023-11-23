package com.bhuwanupadhyay.mtts.domain.api

import com.bhuwanupadhyay.mtts.domain.search.Search
import com.bhuwanupadhyay.mtts.domain.search.criteria.Criteria
import com.bhuwanupadhyay.mtts.domain.spi.Searches

interface SearchForSpaceTrains {
    val searches: Searches
    infix fun satisfying(criteria: Criteria): Search
}