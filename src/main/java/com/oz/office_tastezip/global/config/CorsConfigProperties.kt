package com.oz.office_tastezip.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cors-config")
open class CorsConfigProperties {
    var allowedOriginPattern: List<String> = emptyList()
}
