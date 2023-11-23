package com.bhuwanupadhyay.mtts.domain.api

import com.bhuwanupadhyay.mtts.domain.spaceport.SpacePort
import com.bhuwanupadhyay.mtts.domain.spi.SpacePorts


interface RetrieveSpacePorts {
    val spacePorts: SpacePorts
    infix fun `having in their name`(partialName: String): Set<SpacePort>
    infix fun `identified by`(id: String): SpacePort
}