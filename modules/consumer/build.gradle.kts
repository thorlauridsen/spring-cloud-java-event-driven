dependencies {
	// The consumer subproject needs access to the event subproject
	implementation(projects.event)

	// Spring Cloud dependencies for AWS SQS
	implementation(local.aws.spring.cloud.sqs)

	// AWS SDK dependencies for SQS
	implementation(local.aws.sdk.sqs)

	// LocalStack dependencies for local AWS services
	implementation(local.localstack.utils)

	// Jackson datatype JSR310 dependency for serializing Java 8 Date/Time API
	implementation(local.jackson.datatype.jsr310)
}
