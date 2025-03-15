# 🦜️🔗 LangChain Rust with Kotlin Multiplatform

[![Latest Version]][crates.io] [![Kotlin]][kotlin-url] [![License]][license-url]

[Latest Version]: https://img.shields.io/crates/v/langchain-rust.svg
[crates.io]: https://crates.io/crates/langchain-rust
[Kotlin]: https://img.shields.io/badge/kotlin-1.9.22-blue.svg
[kotlin-url]: https://kotlinlang.org
[License]: https://img.shields.io/badge/license-MIT-green.svg
[license-url]: https://opensource.org/licenses/MIT

⚡ Building applications with LLMs through composability, with Rust and Kotlin Multiplatform! ⚡

[![Discord](https://dcbadge.vercel.app/api/server/JJFcTFbanu?style=for-the-badge)](https://discord.gg/JJFcTFbanu)
[![Docs: Tutorial](https://img.shields.io/badge/docs-tutorial-success?style=for-the-badge&logo=appveyor)](https://langchain-rust.sellie.tech/get-started/quickstart)

## 🤔 What is this?

This is the Rust language implementation of [LangChain](https://github.com/langchain-ai/langchain) with Kotlin Multiplatform bindings. The library enables seamless integration with large language models (LLMs) across multiple platforms:

- **Android** applications via JVM bindings
- **iOS** applications via Kotlin/Native
- **Desktop** applications (Windows, macOS, Linux)
- **Server-side** JVM applications

The Rust core provides performance-critical processing while the Kotlin Multiplatform layer offers an idiomatic API for developers across all platforms.

## 🚀 Supported Platforms

| Platform | Support | Status |
|----------|---------|--------|
| JVM (Desktop) | ✅ | Stable |
| Android | ✅ | Stable |
| iOS | ✅ | Stable |
| macOS | ✅ | Stable |
| Linux | ✅ | Stable |
| Windows | ✅ | Stable |
| Web (JS) | 🔄 | Planned |

### Platform Requirements

- **Android**: minSdk 21 or higher
- **iOS**: iOS 14.0+
- **JVM**: Java 11 or newer
- **Native**: Latest Kotlin/Native compiler

## 📦 Installation

Add the library to your Kotlin project:

### Kotlin Multiplatform

```kotlin
implementation("io.github.kashif-mehmood-km:langchain-kmp:0.1.0")
```

### JVM/Android

```kotlin
implementation("io.github.kashif-mehmood-km:langchain-kmp-jvm:0.1.0")
implementation("io.github.kashif-mehmood-km:langchain-kmp-android:0.1.0")
```

### iOS (via CocoaPods)

```ruby
pod 'langchain_kmp', '~> 0.1.0'
```

## 🔍 Basic Usage

```kotlin
import ai.langchain.kmp.LangChainSample
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val langChain = LangChainSample()
    
    // Get version
    val version = langChain.getVersion()
    println("LangChain Rust version: $version")
    
    // Invoke an LLM model
    val response = langChain.invokeModel(
        prompt = "Explain Rust and Kotlin interoperability", 
        modelName = "gpt-4o-mini"
    )
    println("Model response: $response")
    
    // List available models
    val models = langChain.listModels()
    models.forEach { 
        println("Model: ${it.name}, Size: ${it.getFormattedSize()}")
    }
}
```

## 🧰 Features

### Core Functionality

- **LLM Integration**: OpenAI, Azure OpenAI, Ollama, Anthropic Claude
- **Embeddings**: OpenAI, Azure OpenAI, Ollama, FastEmbed, MistralAI
- **VectorStores**: OpenSearch, Postgres, Qdrant, Sqlite, SurrealDB
- **Chains**: LLM Chain, Conversational Chain, Sequential Chain, Q&A Chain, SQL Chain
- **Agents and Tools**: Chat Agent, Search, Wolfram/Math, Command Line
- **Document Loaders**: PDF, Pandoc, HTML, CSV, Git, Source Code

### Multiplatform Benefits

- **High Performance**: Native Rust implementation for performance-critical operations
- **Memory Efficiency**: Zero-copy rendering and optimized data structures
- **Cross-Platform**: Same API across all platforms
- **Type Safety**: Full Kotlin type system with proper error handling

## 🔨 Building from Source

### Prerequisites

- Rust (latest stable)
- Cargo
- JDK 11+
- Kotlin 1.9.22+
- Gradle 8.0+

### Build Steps

```bash
# Clone the repository
git clone https://github.com/kashif-mehmood-km/langchain-rust.git
cd langchain-rust

# Build the Rust library
cargo build --release

# Generate Kotlin bindings and build KMP library
./scripts/generate-kmp-bindings.sh

# Publish to Maven Local (for testing)
cd langchain-kmp
./gradlew publishToMavenLocal
```

## 🧠 How It Works

This library uses [UniFFI](https://github.com/mozilla/uniffi-rs) and [Gobley](https://github.com/gobley/gobley) to generate Kotlin bindings for the Rust implementation. The architecture consists of:

1. **Rust Core**: High-performance implementation of LangChain functionality
2. **FFI Layer**: Bridge between Rust and Kotlin using UniFFI
3. **Kotlin API**: Idiomatic Kotlin interface for all platforms
4. **Platform-Specific Loaders**: Optimized native library loading for each platform

## 📚 Documentation

- [Getting Started Guide](https://langchain-rust.sellie.tech/get-started/quickstart)
- [API Reference](https://langchain-rust.sellie.tech/reference/)
- [Examples](https://github.com/kashif-mehmood-km/langchain-rust/tree/main/examples)

## 👨‍💻 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgements

- [LangChain](https://github.com/langchain-ai/langchain) - Original LangChain implementation
- [UniFFI](https://github.com/mozilla/uniffi-rs) - Mozilla's FFI binding generator
- [Gobley](https://github.com/gobley/gobley) - Kotlin Multiplatform bindings generator for UniFFI
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - Kotlin's cross-platform framework