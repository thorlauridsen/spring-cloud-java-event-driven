plugins {
    alias(local.plugins.lombok)
    alias(local.plugins.springboot)
    alias(local.plugins.spring.dependencies)
}

dependencies {
    // The event subproject needs access to the model subproject
    implementation(projects.model)

    // Spring Boot dependencies
    implementation(local.springboot.starter)

    // Jackson datatype JSR310 dependency for serializing Java 8 Date/Time API
    implementation(local.jackson.datatype.jsr310)

}

// Disabling bootJar and bootRun is necessary for a subproject/module
// that uses the Spring Boot plugin but is not supposed to be executable.
tasks.bootJar {
    enabled = false
}
tasks.bootRun {
    enabled = false
}
