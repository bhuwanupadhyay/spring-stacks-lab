package com.bhuwanupadhyay.mtts.domain.stubs

import com.bhuwanupadhyay.mtts.domain.search.Search
import com.bhuwanupadhyay.mtts.domain.spi.Searches
import java.util.UUID

@Stub
class InMemorySearches : Searches {

    val searches: MutableMap<UUID, Search> = hashMapOf()

    override fun `find search identified by`(searchId: UUID): Search? = searches[searchId]
    override fun save(search: Search): Search {
        searches[search.id] = search
        return search
    }
}