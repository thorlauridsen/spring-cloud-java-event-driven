plugins {
	alias(local.plugins.lombok)
}

dependencies {
	// The consumer subproject needs access to the event and model subprojects
	implementation(projects.event)
	implementation(projects.model)

	// Spring Boot dependencies
	implementation(local.springboot.starter.jackson)

	// Spring Cloud dependencies for AWS SQS
	implementation(local.aws.spring.cloud.sqs)

	// Jackson datatype JSR310 dependency for serializing Java 8 Date/Time API
	implementation(local.jackson.datatype.jsr310)
}
