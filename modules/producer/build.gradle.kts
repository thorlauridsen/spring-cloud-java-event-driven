plugins {
	alias(local.plugins.lombok)
}

dependencies {
	// The producer subproject needs access to the event and model subprojects
	implementation(projects.event)
	implementation(projects.model)

	// Spring Cloud dependencies for AWS SNS
	implementation(local.aws.spring.cloud.sns)
}
