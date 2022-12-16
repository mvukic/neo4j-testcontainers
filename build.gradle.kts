import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.0"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.0-RC"
	kotlin("plugin.spring") version "1.8.0-RC"
	kotlin("plugin.serialization") version "1.8.0-RC"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(19))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["testcontainersVersion"] = "1.17.6"

dependencies {
	// Spring Boot dependency
	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// Kotlin dependencies
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

	// Testing dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:neo4j")
	testImplementation("io.mockk:mockk:1.13.3")
	testImplementation("com.ninja-squad:springmockk:4.0.0")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "19"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.wrapper {
    version = "7.6"
}