# LangChain Kotlin Multiplatform

## Publishing to Maven Local

To build and publish this library to your local Maven repository for testing:

```bash
cd langchain-kmp
./gradlew publishToMavenLocal
```

If you want to clean the build before publishing (recommended):

```bash
./gradlew cleanAndPublishToMavenLocal
```

## Using in Another Project

After publishing to Maven Local, you can use the library in other Kotlin projects.

### Kotlin Multiplatform Project

```kotlin
// In your build.gradle.kts
repositories {
    mavenLocal() // Add this before other repositories
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("ai.langchain:langchain-kmp:0.1.0")
            }
        }
    }
}
```

### JVM/Android Project

```kotlin
// In your build.gradle.kts or build.gradle
repositories {
    mavenLocal() // Add this before other repositories
    mavenCentral()
}

dependencies {
    // For JVM
    implementation("ai.langchain:langchain-kmp-jvm:0.1.0")
    
    // For Android
    implementation("ai.langchain:langchain-kmp-android:0.1.0")
}
```

## Sample Usage

```kotlin
import ai.langchain.kmp.LangChainSample
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val langChain = LangChainSample()
    
    // Get library version
    println("LangChain version: ${langChain.getVersion()}")
    
    // Simple LLM call
    val response = langChain.simpleLlmInvocation("What is LangChain?")
    println("Response: $response")
    
    // Chat with LLM
    val llm = langChain.createLlm()
    val result = langChain.chatWithLlm(
        llm,
        "You are a helpful assistant.",
        "Tell me about Kotlin Multiplatform and Rust interoperability."
    )
    println("Text: ${result.text}")
    println("Tokens: ${result.totalTokens ?: "unknown"}")
}
```

## Platforms Supported

- JVM
- Android (arm64-v8a, armeabi-v7a, x86, x86_64)
- iOS (arm64, x64 simulator)
- macOS (arm64, x64)
- Linux (x64)
- Windows (x64)

## Requirements

Make sure to set the appropriate API keys for the LLM provider you're using (e.g., OpenAI) in your application.
