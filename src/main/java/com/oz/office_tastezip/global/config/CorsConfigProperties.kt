package com.oz.office_tastezip.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cors-config")
public class CorsConfigProperties {
    private List<String> allowedOriginPattern;
}
