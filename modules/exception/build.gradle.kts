plugins {
    alias(local.plugins.lombok)
    alias(local.plugins.springboot)
    alias(local.plugins.spring.dependencies)
}

dependencies {
    // Spring Boot dependencies
    implementation(local.springboot.starter)
    implementation(local.springboot.starter.webmvc)

    // Springdoc OpenAPI for providing Swagger documentation
    implementation(local.springdoc.openapi.starter.webmvc)
}

// Disabling bootJar and bootRun is necessary for a subproject/module
// that uses the Spring Boot plugin but is not supposed to be executable.
tasks.bootJar {
    enabled = false
}
tasks.bootRun {
    enabled = false
}
