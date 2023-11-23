package com.bhuwanupadhyay.mtts.infra.rest.controllers

import com.bhuwanupadhyay.mtts.domain.api.RetrieveSpacePorts
import com.bhuwanupadhyay.mtts.infra.rest.resources.spaceport.SpacePort
import com.bhuwanupadhyay.mtts.infra.rest.resources.spaceport.SpacePorts
import com.bhuwanupadhyay.mtts.infra.rest.resources.spaceport.asResourcesFor
import com.bhuwanupadhyay.mtts.infra.rest.resources.spaceport.toResource
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit.MINUTES

@RestController
@RequestMapping("/spaceports")
class SpacePortsController(private val retrieveSpacePorts: RetrieveSpacePorts) {

    companion object {
        const val WHATEVER = ""
    }

    @GetMapping
    fun getSpacePorts(
        @RequestParam(
            name = "withNameContaining",
            required = false
        ) partialName: String?
    ): ResponseEntity<SpacePorts> {
        val spacePorts = retrieveSpacePorts `having in their name` (partialName ?: WHATEVER)
        return ok()
            .cacheControl(CacheControl.maxAge(60, MINUTES))
            .body(
                spacePorts.asResourcesFor(partialName)
            )
    }

    @GetMapping(path = ["/{id}"])
    fun getSpacePortIdentifiedBy(@PathVariable id: String): ResponseEntity<SpacePort> {
        val spacePort = retrieveSpacePorts `identified by` id
        return ok()
            .cacheControl(CacheControl.maxAge(60, MINUTES))
            .body(spacePort.toResource())
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(response: HttpServletResponse, exception: NoSuchElementException) {
        response.sendError(SC_NOT_FOUND, exception.message)
    }
}