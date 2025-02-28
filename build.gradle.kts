plugins {
    kotlin("jvm") version "1.5.31"
}

group = "com.luissoares"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.javalin:javalin:4.+")
    implementation("org.slf4j:slf4j-simple:1.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.+")

    implementation("org.jetbrains.exposed:exposed:0.+")
    implementation("mysql:mysql-connector-java:8.+")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
    testImplementation("io.mockk:mockk:1.+")
    testImplementation("org.skyscreamer:jsonassert:1.+")

    testImplementation("org.testcontainers:testcontainers:1.+")
    testImplementation("org.testcontainers:mysql:1.+")

    testImplementation("au.com.dius:pact-jvm-consumer-junit5:4.+")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("pact.rootDir", "src/main/resources/cdc-pact-tests")
}