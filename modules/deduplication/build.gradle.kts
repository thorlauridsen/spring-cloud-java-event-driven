import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	alias(local.plugins.lombok)
	alias(local.plugins.springboot)
	alias(local.plugins.spring.dependencies)
}

dependencies {
	// The outbox subproject needs access to the model subproject
	implementation(projects.model)

	// Spring Boot dependencies
	implementation(local.springboot.starter.jpa)
}

// Disabling bootJar and bootRun is necessary for a subproject/module
// that uses the Spring Boot plugin but is not supposed to be executable.
tasks.named<BootJar>("bootJar") {
	enabled = false
}
tasks.named<BootRun>("bootRun") {
	enabled = false
}
