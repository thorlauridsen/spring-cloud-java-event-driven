dependencies {
	// Spring Cloud dependencies for AWS SNS
	implementation(local.aws.spring.cloud.sns)

	// AWS SDK dependencies for SNS
	implementation(local.aws.sdk.sns)

	// LocalStack dependencies for local AWS services
	implementation(local.localstack.utils)
}
