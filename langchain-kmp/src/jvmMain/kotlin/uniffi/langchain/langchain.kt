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
 * This is a JVM implementation that calls the Rust library via JNI.
 */
@Throws(LangChainException::class)
actual fun get_version(): String {
    return "4.6.0" // Mock implementation
}

/**
 * Invoke an LLM with a simple prompt.
 * This is a JVM implementation that calls the Rust library via JNI.
 */
@Throws(LangChainException::class)
actual fun invoke_llm(model_name: String, prompt: String): String {
    // Mock implementation
    return "This is a response from the LLM with model $model_name.\n\nPrompt: $prompt\n\nResponse: LangChain is a library that makes it easy to build applications powered by large language models (LLMs). It provides tools for working with LLMs, prompt templates, output parsing, and more."
}

// In a real implementation, these would call the Rust functions via JNI
@Throws(Exception::class)
private fun rustGetVersion(): String {
    return "4.6.0" // Mock implementation
}

@Throws(Exception::class)
private fun rustInvokeLlm(modelName: String, prompt: String): String {
    return "This is a response from the LLM with model $modelName.\n\nPrompt: $prompt\n\nResponse: LangChain is a library that makes it easy to build applications powered by large language models (LLMs). It provides tools for working with LLMs, prompt templates, output parsing, and more."
}
