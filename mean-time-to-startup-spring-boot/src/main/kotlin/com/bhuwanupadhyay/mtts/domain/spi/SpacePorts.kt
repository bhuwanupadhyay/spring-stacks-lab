package com.bhuwanupadhyay.mtts.domain.spi

import com.bhuwanupadhyay.mtts.domain.spaceport.SpacePort

interface SpacePorts {
    fun getAllSpacePorts(): Set<SpacePort>
}