plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "7.0.2"
	id("jacoco")
}

apply(plugin = "com.diffplug.spotless")

jacoco {
	toolVersion = "0.8.12"
}

spotless {
	java {
		googleJavaFormat()
			.aosp()
			.reorderImports(true)
		removeUnusedImports()
	}
}

group = "com.douglasmarq"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

tasks.named("build") {
	dependsOn("bootJar")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("bff-service.jar")
}

extra["springCloudVersion"] = "2024.0.0"

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//Database
	runtimeOnly("org.postgresql:postgresql")

	// Api integration
	implementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")

	//Utils
	compileOnly("org.projectlombok:lombok:1.18.36")
	annotationProcessor("org.projectlombok:lombok:1.18.36")

	// Stateless Authentication
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	//Logging and code formatting
	implementation("net.logstash.logback:logstash-logback-encoder:8.0")
	implementation("com.google.googlejavaformat:google-java-format:1.25.2")

	// Testing frameworks and tools
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.security:spring-security-test")
	testCompileOnly("org.projectlombok:lombok:1.18.36")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<JacocoReport>("jacocoTestReport") {
	dependsOn(tasks.test)
	executionData.setFrom(fileTree(project.buildDir).include("/jacoco/*.exec"))
	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}
}

tasks.withType<Test> {
	testLogging {
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
		showStandardStreams = true
	}

	extensions.configure<JacocoTaskExtension> {
		setDestinationFile(file("$buildDir/jacoco/jacocoTest.exec"))
	}
}

