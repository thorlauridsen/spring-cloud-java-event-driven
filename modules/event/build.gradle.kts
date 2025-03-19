import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    alias(local.plugins.springboot)
    alias(local.plugins.spring.dependencies)
}

dependencies {

    // Spring Boot dependencies
    implementation(local.springboot.starter)

    // Jackson datatype JSR310 dependency for serializing Java 8 Date/Time API
    implementation(local.jackson.datatype.jsr310)

}

// Disabling bootJar and bootRun is necessary for a subproject/module
// that uses the Spring Boot plugin but is not supposed to be executable.
tasks.named<BootJar>("bootJar") {
    enabled = false
}
tasks.named<BootRun>("bootRun") {
    enabled = false
}
