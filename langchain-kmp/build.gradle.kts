plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("maven-publish")
    id("application")
}

group = "ai.langchain"
version = "0.1.0"

// Configure main class for JVM runs
application {
    mainClass.set("ai.langchain.sample.DemoKt")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
            }
        }
        val jvmTest by getting
    }
    
    // Configure all targets for publication
    targets.all {
        compilations.all {
            kotlinOptions {
                // Any specific compiler options needed
            }
        }
    }
}

// Configure the JVM run task
tasks.named<JavaExec>("run") {
    dependsOn("jvmMainClasses")
    val jvmMain = kotlin.jvm().compilations.getByName("main")
    classpath = jvmMain.output.classesDirs + jvmMain.runtimeDependencyFiles
    mainClass.set("ai.langchain.sample.DemoKt")
}

// Configure publishing
publishing {
    repositories {
        // Add Maven Local repository
        mavenLocal()
    }
}

// Task to clean and publish to Maven Local
tasks.register("cleanAndPublishToMavenLocal") {
    group = "publishing"
    description = "Clean build and publish the library to Maven Local repository"
    
    dependsOn("clean")
    finalizedBy("publishToMavenLocal")
}
