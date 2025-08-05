package com.oz.office_tastezip.domain.notice.controller

import com.oz.office_tastezip.domain.notice.NoticeService
import com.oz.office_tastezip.domain.notice.dto.NoticeResponse
import com.oz.office_tastezip.domain.notice.dto.NoticeUpdateDto
import com.oz.office_tastezip.domain.notice.dto.SetPinRequest
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.global.aspect.AdminOnly
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseSuccess
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "공지 관련 컨트롤러", description = "NOTICE CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    private val log = KotlinLogging.logger {}

    @Operation(summary = "공지 조회")
    @GetMapping
    fun getNotice(
        @RequestParam searchType: SearchType = SearchType.TOTAL,
        @RequestParam searchContent: String = "",
        @PageableDefault(size = 10, page = 0, sort = ["updatedAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Response.Body<List<NoticeResponse>>> {
        val notices = noticeService.searchNotices(searchType, searchContent, pageable)

        val total = notices.totalElements
        val offset = pageable.offset

        val noticeResponses = notices.content.mapIndexed { index, notice ->
            val sequence = (total - offset - index).toInt()
            NoticeResponse.of(notice, sequence)
        }

        return ResponseSuccess<List<NoticeResponse>>().success(noticeResponses)
    }

    @Operation(summary = "공지 상세 조회 + 조회 수 증가")
    @GetMapping("{id}/detail")
    fun getNoticeDetail(@PathVariable(name = "id") id: String): ResponseEntity<Response.Body<NoticeResponse>> {
        val notice = noticeService.searchNotice(UUID.fromString(id))
        return ResponseSuccess<NoticeResponse>().success(NoticeResponse.of(notice))
    }

    @Operation(summary = "공지 등록")
    @AdminOnly
    @PostMapping
    fun insertNotice(@RequestBody @Valid noticeUpdateDto: NoticeUpdateDto): ResponseEntity<Response.Body<String>> {
        return ResponseSuccess<String>().success("공지가 등록되었습니다.")
    }

    @Operation(summary = "공지 수정")
    @AdminOnly
    @PutMapping
    fun updateNotice(@RequestBody @Valid noticeUpdateDto: NoticeUpdateDto): ResponseEntity<Response.Body<String>> {
        return ResponseSuccess<String>().success()
    }

    @Operation(summary = "공지 삭제")
    @AdminOnly
    @DeleteMapping("/{id}")
    fun deleteNotice(@PathVariable(name = "id") id: String): ResponseEntity<Response.Body<String>> {
        return ResponseSuccess<String>().success("공지가 삭제되었습니다.")
    }

    @Operation(summary = "핀 설정(상단 노출)")
    @AdminOnly
    @PatchMapping("pin")
    fun setPinByOrgId(@RequestBody setPinRequest: SetPinRequest): ResponseEntity<Response.Body<String>> {

        return ResponseSuccess<String>().success("상단 노출이 설정되었습니다.")
    }
}
