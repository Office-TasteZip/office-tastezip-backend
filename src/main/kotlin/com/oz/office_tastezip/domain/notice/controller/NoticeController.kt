package com.oz.office_tastezip.domain.notice.controller

import com.oz.office_tastezip.domain.notice.NoticeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@Tag(name = "공지 관련 컨트롤러", description = "NOTICE CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/notices")
class NoticeController(
    private val noticeService: NoticeService
) {

    private val log = KotlinLogging.logger {}

    @Operation(summary = "공지 조회")
    @GetMapping
    fun getNotice() {

    }

    @Operation(summary = "공지 상세 조회 + 조회 수 증가")
    @GetMapping("/detail")
    fun getNoticeDetail() {

    }

    @Operation(summary = "공지 등록")
    @PostMapping
    fun insertNotice() {

    }

    @Operation(summary = "공지 수정")
    @PutMapping
    fun updateNotice() {

    }

    @Operation(summary = "공지 삭제")
    @DeleteMapping
    fun deleteNotice() {

    }

    @Operation(summary = "핀 설정(상단 노출)")
    @PostMapping("/pin")
    fun setPinByOrgId() {

    }
}
