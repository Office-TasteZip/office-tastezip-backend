package com.oz.office_tastezip.domain.restaurant.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "식당 컨트롤러", description = "RESTAURANT CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/restaurants")
class RestaurantController {
}
