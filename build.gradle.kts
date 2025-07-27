plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("kapt") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.oz"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val querydslVersion = "5.1.0"

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.19.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.19.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.1")

    // 기타 유틸
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("org.apache.commons:commons-configuration2:2.12.0")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // QueryDSL
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Kotest
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
