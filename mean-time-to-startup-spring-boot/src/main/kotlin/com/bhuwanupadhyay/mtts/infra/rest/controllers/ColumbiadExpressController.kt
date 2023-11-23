package com.bhuwanupadhyay.mtts.infra.rest.controllers

import com.bhuwanupadhyay.mtts.infra.rest.resources.Resource
import org.springframework.hateoas.IanaLinkRelations.SELF
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.add
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class ColumbiadExpressController {

    @GetMapping
    fun root(): ColumbiadExpress {
        return ColumbiadExpress()
                .add(linkTo(SearchController::class.java).withRel("searches"))
                .add(linkTo(BookingController::class.java).withRel("bookings"))
                .add(ColumbiadExpressController::class) {
                    linkTo { root() } withRel SELF
                }
                .add(SpacePortsController::class) {
                    linkTo { getSpacePorts(null) } withRel "spaceports"
                }

    }

}

@Resource
class ColumbiadExpress : RepresentationModel<ColumbiadExpress>()