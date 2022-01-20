import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    idea
    application
    kotlin("jvm")
    id("com.google.protobuf")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

dependencies {
    protobuf(project(":protos"))
    api(kotlin("stdlib"))

    api("com.google.protobuf:protobuf-kotlin:${rootProject.ext["protobufVersion"]}")
    api("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    api("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
    runtimeOnly("io.grpc:grpc-netty:${rootProject.ext["grpcVersion"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.ext["coroutinesVersion"]}")

    testImplementation("io.grpc:grpc-testing:${rootProject.ext["grpcVersion"]}")

    implementation("com.google.guava:guava:31.0.1-jre")

    // Koin Dependency injection
    implementation("io.insert-koin:koin-core:3.1.5")
    testImplementation("io.insert-koin:koin-test:3.1.5")
    testImplementation("io.insert-koin:koin-test-junit5:3.1.5")

    // Use the Kotlin test library.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.ext["grpcKotlinVersion"]}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

idea {
    module {
        generatedSourceDirs.addAll(
            listOf(
                file("${protobuf.protobuf.generatedFilesBaseDir}/main/grpc"),
                file("${protobuf.protobuf.generatedFilesBaseDir}/main/java")
            )
        )
    }
}
