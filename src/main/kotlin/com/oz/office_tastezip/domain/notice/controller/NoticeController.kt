package com.oz.office_tastezip.domain.notice.controller

import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.domain.notice.NoticeService
import com.oz.office_tastezip.domain.notice.dto.NoticeResponse
import com.oz.office_tastezip.domain.notice.dto.NoticeUpdateDto
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.global.aspect.AdminOnly
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseSuccess
import com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "공지 컨트롤러", description = "NOTICE CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/notices")
class NoticeController(
    private val noticeService: NoticeService
) {

    @Operation(summary = "공지 조회")
    @GetMapping
    fun getNotice(
        @RequestParam searchType: SearchType = SearchType.TOTAL,
        @RequestParam searchContent: String = "",
        @PageableDefault(size = 10, page = 0, sort = ["updatedAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Response.Body<List<NoticeResponse>>> {
        val notices = noticeService.searchNotices(searchType, searchContent, pageable)

        val noticeResponses = notices.content.map { notice ->
            NoticeResponse.summaryOf(notice)
        }

        return ResponseSuccess<List<NoticeResponse>>().success(noticeResponses)
    }

    @Operation(summary = "공지 상세 조회 + 조회 수 증가")
    @GetMapping("{id}/detail")
    fun getNoticeDetail(@PathVariable(name = "id") id: String): ResponseEntity<Response.Body<NoticeResponse>> {
        val notice = noticeService.searchNotice(UUID.fromString(id))
        return ResponseSuccess<NoticeResponse>().success(NoticeResponse.detailOf(notice))
    }

    @Operation(summary = "공지 등록")
    @AdminOnly
    @PostMapping
    fun insertNotice(@RequestBody @Valid noticeUpdateDto: NoticeUpdateDto): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        val notice = Notice.from(noticeUpdateDto, userDetails.nickname)

        noticeService.insertNotice(notice)
        return ResponseSuccess<String>().success("공지가 등록되었습니다.")
    }

    @Operation(summary = "공지 수정")
    @AdminOnly
    @PatchMapping("/{id}")
    fun updateNotice(
        @PathVariable(name = "id") id: String,
        @RequestBody @Valid noticeUpdateDto: NoticeUpdateDto
    ): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        noticeService.updateNotice(UUID.fromString(id), userDetails.nickname, noticeUpdateDto)
        return ResponseSuccess<String>().success("공지가 수정되었습니다.")
    }

    @Operation(summary = "공지 삭제")
    @AdminOnly
    @DeleteMapping("/{id}")
    fun deleteNotice(@PathVariable(name = "id") id: String): ResponseEntity<Response.Body<String>> {
        noticeService.deleteNotice(UUID.fromString(id))
        return ResponseSuccess<String>().success("공지가 삭제되었습니다.")
    }

    @Operation(summary = "핀 설정 상태 변경(상단 노출 여부)")
    @AdminOnly
    @PatchMapping("/{id}/pin")
    fun setPinByOrgId(
        @PathVariable(name = "id") id: String,
        @RequestParam(name = "isPinned") isPinned: Boolean
    ): ResponseEntity<Response.Body<String>> {
        noticeService.updatePinnedStatus(UUID.fromString(id), isPinned)
        return ResponseSuccess<String>().success("상단 노출이 설정되었습니다.")
    }
}
