package com.oz.office_tastezip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class OfficeTasteZipApplication

fun main(args: Array<String>) {
    runApplication<OfficeTasteZipApplication>(*args)
}
