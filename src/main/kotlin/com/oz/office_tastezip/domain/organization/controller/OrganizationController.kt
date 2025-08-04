package com.oz.office_tastezip.domain.organization.controller;

import com.oz.office_tastezip.domain.organization.OrganizationService
import com.oz.office_tastezip.domain.organization.dto.SearchOrganizationNameDto
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseSuccess
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "조직/기업 정보 컨트롤러", description = "ORGANIZATION CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/organizations")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    private val log = KotlinLogging.logger {}

    @Operation(summary = "기업 이름 검색")
    @GetMapping("/name-search")
    fun searchOrganizationName(
        @RequestParam(name = "name") name: String,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<List<SearchOrganizationNameDto>>> {
        log.info { "${httpServletRequest.remoteAddr}|search organization name: $name" }
        val response = organizationService.findOrganizationByName(name)
        return ResponseSuccess<List<SearchOrganizationNameDto>>().success(response)
    }


}
