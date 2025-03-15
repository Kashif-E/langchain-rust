plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("maven-publish")
    id("signing")
    id("application")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "io.github.kashif-mehmood-km"
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
    
    // Native targets
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "langchain-kmp"
            isStatic = true
        }
    }
    
    // Optional Android target (uncomment if needed)
    /*
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    */
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
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

// Maven Publishing Configuration
mavenPublishing {
    coordinates(
        groupId = "io.github.kashif-mehmood-km",
        artifactId = "langchain-kmp",
        version = "0.1.0"
    )
    pom {
        name.set("LangChain KMP")
        description.set("LangChain Library for Kotlin Multiplatform with Rust backend")
        inceptionYear.set("2025")
        url.set("https://github.com/kashif-mehmood-km/langchain-rust")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("kashif-mehmood-km")
                name.set("Kashif")
                email.set("your.email@example.com")
            }
        }
        scm {
            url.set("https://github.com/kashif-mehmood-km/langchain-rust")
        }
    }
    // Configure publishing to Maven Central
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    // Enable GPG signing for all publications
    signAllPublications()
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
