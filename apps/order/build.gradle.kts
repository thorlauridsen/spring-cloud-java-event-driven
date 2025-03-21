plugins {
	alias(local.plugins.springboot)
	alias(local.plugins.spring.dependencies)
}

dependencies {
	// The order subproject needs access to the following subprojects
	implementation(projects.consumer)
	implementation(projects.deduplication)
	implementation(projects.event)
	implementation(projects.jackson)
	implementation(projects.model)
	implementation(projects.outbox)
	implementation(projects.producer)

	// Spring Boot dependencies
	implementation(local.springboot.starter)
	implementation(local.springboot.starter.jpa)
	implementation(local.springboot.starter.web)

	// Spring Cloud dependencies for AWS SQS and SNS
	implementation(local.aws.spring.cloud.sqs)
	implementation(local.aws.spring.cloud.sns)

	// Jackson datatype JSR310 dependency for serializing Java 8 Date/Time API
	implementation(local.jackson.datatype.jsr310)

	// H2 database dependency for in-memory database
	implementation(local.h2database)

	// Liquibase core dependency for database migrations
	implementation(local.liquibase.core)

	// Springdoc OpenAPI for providing Swagger documentation
	implementation(local.springdoc.openapi.starter.webmvc)

	// Spring Boot test dependencies
	testImplementation(local.springboot.starter.test)

	// JUnit platform launcher dependency for running JUnit tests
	testRuntimeOnly(local.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
