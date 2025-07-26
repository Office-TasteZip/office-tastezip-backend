package com.oz.office_tastezip.global.constant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public enum TimeFormat {
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
    UTCTime("yyMMddHHmmss'Z'") {
        @Override
        public String getString() {
            return LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        }

        @Override
        public String getString(long timesInMillis) {
            return Instant.ofEpochMilli(timesInMillis).atZone(ZoneOffset.UTC).toLocalDateTime().format(formatter);
        }

        @Override
        public String getString(LocalDateTime datetime) {
            return datetime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(formatter);
        }
    };

    public final String format;
    public final DateTimeFormatter formatter;

    TimeFormat(String format) {
        this.format = format;
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    /**
     * 현재시간
     */
    public String getString() {
        return LocalDateTime.now().format(formatter);
    }

    /**
     * millisecond → formatted string
     */
    public String getString(long timesInMillis) {
        return Instant.ofEpochMilli(timesInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(formatter);
    }

    /**
     * LocalDateTime → formatted string
     */
    public String getString(LocalDateTime datetime) {
        return datetime.format(formatter);
    }
}
