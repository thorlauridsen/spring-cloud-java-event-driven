plugins {
	java
}

group = "com.github.thorlauridsen"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

subprojects {
	apply(plugin = "java")
}
