package com.oz.office_tastezip.global.config

import com.oz.office_tastezip.global.interceptor.GlobalRequestInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val globalRequestInterceptor: GlobalRequestInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(globalRequestInterceptor)
            .addPathPatterns("/**")
    }
}
