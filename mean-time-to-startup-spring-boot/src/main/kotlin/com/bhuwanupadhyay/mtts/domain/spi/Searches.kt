package com.bhuwanupadhyay.mtts.domain.spi

import com.bhuwanupadhyay.mtts.domain.search.Search
import java.util.UUID

interface Searches {
    infix fun `find search identified by`(searchId: UUID): Search?
    infix fun save(search: Search): Search
}