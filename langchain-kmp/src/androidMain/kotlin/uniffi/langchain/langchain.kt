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
 * This is an Android implementation that calls the Rust library via JNI.
 */
@Throws(LangChainException::class)
actual fun get_version(): String {
    return try {
        rustGetVersion()
    } catch (e: Exception) {
        throw LangChainException.UnknownException("Failed to get version: ${e.message}")
    }
}

/**
 * Invoke an LLM with a simple prompt.
 * This is an Android implementation that calls the Rust library via JNI.
 */
@Throws(LangChainException::class)
actual fun invoke_llm(model_name: String, prompt: String): String {
    return try {
        rustInvokeLlm(model_name, prompt)
    } catch (e: Exception) {
        throw LangChainException.ApiException("Failed to invoke LLM: ${e.message}")
    }
}

// Native methods that call into the Rust library via JNI
@Throws(Exception::class)
private external fun rustGetVersion(): String

@Throws(Exception::class)
private external fun rustInvokeLlm(modelName: String, prompt: String): String

// Initialize JNI
private object JniInitializer {
    init {
        try {
            System.loadLibrary("langchain_rust")
        } catch (e: Exception) {
            println("Failed to load native library: ${e.message}")
            e.printStackTrace()
        }
    }
}

private val jniInitialized = JniInitializer
