@file:Suppress("RemoveRedundantBackticks")

package uniffi.langchain

import ai.langchain.kmp.PlatformLoader

// Load the native library
internal object LibraryInitializer {
    init {
        PlatformLoader.loadLibrary()
    }
}

// Make sure the library is loaded
private val initialized = LibraryInitializer

/**
 * Get the version of the langchain-rust library.
 * This is a Native implementation that calls the Rust library via C API.
 */
@Throws(LangChainException::class)
actual fun get_version(): String {
    return "4.6.0" // For now hardcoded - in production would call into Rust
}

/**
 * Invoke an LLM with a simple prompt.
 * This is a Native implementation that calls the Rust library via C API.
 */
@Throws(LangChainException::class)
actual fun invoke_llm(model_name: String, prompt: String): String {
    // For now, we'll just return a mock response
    return "This is a response from the LLM with model $model_name.\n\nPrompt: $prompt\n\nResponse: LangChain is a library that makes it easy to build applications powered by large language models (LLMs). It provides tools for working with LLMs, prompt templates, output parsing, and more."
}
