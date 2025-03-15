# 🦜️🔗 LangChain Rust with Kotlin Multiplatform

This is the Rust language implementation of [LangChain](https://github.com/langchain-ai/langchain) with Kotlin Multiplatform bindings that enable using the library on iOS, Android, desktop, and web applications.

## Using the Library

Add the library to your Kotlin project:

```kotlin
// For a Kotlin Multiplatform project
implementation("io.github.kashif-mehmood-km:langchain-kmp:0.1.0")

// For a JVM-only project
implementation("io.github.kashif-mehmood-km:langchain-kmp-jvm:0.1.0")

// For Android projects
implementation("io.github.kashif-mehmood-km:langchain-kmp-android:0.1.0")
```

Basic usage example:

```kotlin
import ai.langchain.kmp.LangChainSample
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val langChain = LangChainSample()
    
    // Get version
    val version = langChain.getVersion()
    println("LangChain Rust version: $version")
    
    // Invoke a model
    val response = langChain.invokeModel(
        prompt = "Explain Rust and Kotlin interoperability", 
        modelName = "gpt-4o-mini"
    )
    println("Model response: $response")
}
```

## Features

- High-performance Rust implementation with Kotlin bindings
- Cross-platform support (JVM, Android, iOS, Native)
- LLM integration with OpenAI, Anthropic, Ollama
- Error handling and resource management

## Building from Source

```bash
# Build the Rust library
cargo build --release

# Generate Kotlin bindings
./scripts/generate-kmp-bindings.sh

# Publish to Maven Local
cd langchain-kmp
./gradlew publishToMavenLocal
```
