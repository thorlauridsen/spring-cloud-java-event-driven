dependencies {
	// The producer subproject needs access to the event subproject
	implementation(projects.event)

	// Spring Cloud dependencies for AWS SNS
	implementation(local.aws.spring.cloud.sns)
}
