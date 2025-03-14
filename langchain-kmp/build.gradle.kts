plugins {
    kotlin("multiplatform") version "1.9.22"
    id("com.android.library") version "8.0.0" apply false
    id("maven-publish")
}

group = "ai.langchain"
version = "0.1.0"

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
    
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    
    when {
        hostOs == "Mac OS X" -> {
            macosX64()
            macosArm64()
            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }
        hostOs == "Linux" -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        
        // Native source sets
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        
        // Platform-specific source sets based on the host platform
        when {
            hostOs == "Mac OS X" -> {
                val macosMain by creating {
                    dependsOn(nativeMain)
                }
                val macosTest by creating {
                    dependsOn(nativeTest)
                }
                val macosX64Main by getting {
                    dependsOn(macosMain)
                }
                val macosArm64Main by getting {
                    dependsOn(macosMain)
                }
                val iosMain by creating {
                    dependsOn(nativeMain)
                }
                val iosTest by creating {
                    dependsOn(nativeTest)
                }
                val iosX64Main by getting {
                    dependsOn(iosMain)
                }
                val iosArm64Main by getting {
                    dependsOn(iosMain)
                }
                val iosSimulatorArm64Main by getting {
                    dependsOn(iosMain)
                }
            }
            hostOs == "Linux" -> {
                val linuxMain by creating {
                    dependsOn(nativeMain)
                }
                val linuxX64Main by getting {
                    dependsOn(linuxMain)
                }
            }
            isMingwX64 -> {
                val windowsMain by creating {
                    dependsOn(nativeMain)
                }
                val mingwX64Main by getting {
                    dependsOn(windowsMain)
                }
            }
        }
    }
    
    // Configure native targets to link the Rust library
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // Link to the Rust library
            linkTask.dependsOn(":buildRustLibrary")
            
            // Determine library name based on platform
            val libPrefix = when {
                hostOs == "Mac OS X" || hostOs == "Linux" -> "lib"
                else -> ""
            }
            
            val libSuffix = when {
                hostOs == "Mac OS X" -> "dylib"
                hostOs == "Linux" -> "so"
                else -> "dll"
            }
            
            // Link the static library
            linkerOpts.add("-L${rootDir.parentFile}/target/release")
            linkerOpts.add("-llangchain_rust")
        }
    }
}

// Add a task to build the Rust library
tasks.register("buildRustLibrary", Exec::class) {
    workingDir = rootDir.parentFile
    commandLine("cargo", "build", "--release")
    
    // Set environment variables for UniFFI
    environment("UNIFFI_BINDGEN_TARGET", "kotlin")
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("langchain-kmp")
                description.set("Kotlin Multiplatform bindings for langchain-rust")
                url.set("https://github.com/yourusername/langchain-kmp")
                licenses {
                    license {
                        name.set("MIT")
                    }
                }
                developers {
                    developer {
                        id.set("yourusername")
                        name.set("Your Name")
                        email.set("your.email@example.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            // Repository information would go here
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
