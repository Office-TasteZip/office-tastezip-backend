package com.oz.office_tastezip.global.constant

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class TimeFormat(val pattern: String) {
    FULL("yyyy-MM-dd HH:mm:ss.SSS"),
    LOG("MM/dd HH:mm:ss.SSS"),
    SEC("yyyy-MM-dd HH:mm:ss"),
    MIN("yyyy-MM-dd HH:mm"),
    DATE("yyyy-MM-dd"),
    YMDHMSmmm("yyyyMMddHHmmssSSS"),
    YMDHMSmmmm("yyyyMMddHHmmssSSSS"),
    YMDHMSmm("yyyyMMddHHmmssSS"),
    YMDHMS("yyyyMMddHHmmss"),
    YMDHM("yyyyMMddHHmm"),
    YMD("yyyyMMdd"),
    TIME("HH:mm:ss.SSS"),
    TIME_SEC("HH:mm:ss"),
    TIME_MIN("HH:mm"),
    UTCTime("yyMMddHHmmss'Z'");

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)

    fun formatNow(): String {
        return when (this) {
            UTCTime -> LocalDateTime.now(ZoneOffset.UTC).format(formatter)
            else -> LocalDateTime.now().format(formatter)
        }
    }

    fun format(timeInMillis: Long): String {
        val zone = if (this == UTCTime) ZoneOffset.UTC else ZoneId.systemDefault()
        return Instant.ofEpochMilli(timeInMillis)
            .atZone(zone)
            .toLocalDateTime()
            .format(formatter)
    }

    fun format(datetime: LocalDateTime?): String {
        return if (datetime != null) {
            when (this) {
                UTCTime -> datetime.atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime()
                    .format(formatter)
                else -> datetime.format(formatter)
            }
        } else {
            ""
        }
    }
}
