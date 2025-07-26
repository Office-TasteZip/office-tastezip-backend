package com.oz.office_tastezip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OfficeTasteZipApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficeTasteZipApplication.class, args);
    }

}
