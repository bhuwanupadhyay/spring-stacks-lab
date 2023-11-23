package com.bhuwanupadhyay.mtts.domain.stubs

import com.bhuwanupadhyay.mtts.domain.spaceport.AstronomicalBody.EARTH
import com.bhuwanupadhyay.mtts.domain.spaceport.AstronomicalBody.MOON
import com.bhuwanupadhyay.mtts.domain.spaceport.SpacePort
import com.bhuwanupadhyay.mtts.domain.spi.SpacePorts
import java.util.UUID.nameUUIDFromBytes

@Stub
class InMemorySpacePorts : SpacePorts {

    private val spacePorts: Set<SpacePort> =
            setOf(SpacePort(id = "Vostochny".toUUIDString(), name = "Vostochny Cosmodrome", location = EARTH),
                    SpacePort(id = "Baikonur".toUUIDString(), name = "Baikonur Cosmodrome", location = EARTH),
                    SpacePort(id = "CC".toUUIDString(), name = "Cape Canaveral Air Force Station", location = EARTH),
                    SpacePort(id = "VBG".toUUIDString(), name = "Vandenberg Air Force Base", location = EARTH),
                    SpacePort(id = "KSC".toUUIDString(), name = "John F. Kennedy Space Center", location = EARTH),
                    SpacePort(id = "CSG".toUUIDString(), name = "Centre Spatial Guyanais", location = EARTH),

                    SpacePort(id = "Peary".toUUIDString(), name = "Peary Crater", location = MOON),
                    SpacePort(id = "Malapert".toUUIDString(), name = "Malapert Mountain", location = MOON),
                    SpacePort(id = "Mare Cognitum".toUUIDString(), name = "Mare Cognitum", location = MOON),
                    SpacePort(id = "Aitken basin".toUUIDString(), name = "South Pole–Aitken basin", location = MOON))

    override fun getAllSpacePorts(): Set<SpacePort> {
        return spacePorts.toSet()
    }

    private fun String.toUUIDString(): String = nameUUIDFromBytes(this.toByteArray()).toString()
}