package ai.langchain.kmp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import uniffi.langchain.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * A helper class for LangChain functionality that wraps the Rust implementation.
 */
class LangChainSample {
    /**
     * Get the version of the langchain-rust library.
     * @return The version string.
     */
    suspend fun getVersion(): String {
        return get_version()
    }
    
    /**
     * Get list of available models from Ollama.
     * @return List of model information objects.
     */
    suspend fun listModels(): List<OllamaModel> {
        try {
            val models = mutableListOf<OllamaModel>()
            
            val currentTime = Clock.System.now().epochSeconds
            
            // Sample models for demonstration
            models.add(OllamaModel(
                name = "llama3",
                parameter_size = "8B",
                size = 4_800_000_000,
                modified = currentTime,
                quantization_level = "Q4_K_M"
            ))
            
            models.add(OllamaModel(
                name = "gemma",
                parameter_size = "7B",
                size = 4_200_000_000,
                modified = currentTime,
                quantization_level = "Q4_K_M"
            ))
            
            models.add(OllamaModel(
                name = "mistral",
                parameter_size = "7B",
                size = 4_100_000_000,
                modified = currentTime,
                quantization_level = "Q4_K_M"
            ))
            
            return models
        } catch (e: Exception) {
            throw LangChainException("Failed to list models: ${e.message}")
        }
    }
    
    /**
     * Invoke a language model with a prompt.
     * @param prompt The prompt to send to the LLM.
     * @param modelName The name of the model to use.
     * @return The generated text.
     */
    suspend fun invokeModel(
        prompt: String, 
        modelName: String
    ): String {
        try {
            return invoke_llm(modelName, prompt)
        } catch (e: LangChainException) {
            throw LangChainException("Error invoking model: ${e.message}")
        }
    }
    
    /**
     * Pull a model from Ollama.
     * @param modelName The name of the model to pull.
     * @return True if successful, false otherwise.
     */
    suspend fun pullModel(modelName: String): Boolean {
        // Simplified implementation that would be replaced with actual API calls
        return true
    }
    
    /**
     * Simple LLM invocation with default model.
     * @param prompt The prompt to send to the LLM.
     * @param modelName The name of the model to use, defaults to "gpt-4o-mini".
     * @return The generated text.
     */
    suspend fun simpleLlmInvocation(prompt: String, modelName: String = "gpt-4o-mini"): String {
        return invokeModel(prompt, modelName)
    }
    
    /**
     * Exception class for LangChain-related errors.
     */
    class LangChainException(message: String) : Exception(message)
}

/**
 * Model representing Ollama model information.
 */
@Serializable
data class OllamaModel(
    val name: String,
    val modelfile: String = "",
    val size: Long = 0,
    val modified: Long = 0,
    val format: String = "",
    val families: List<String> = emptyList(),
    val parameter_size: String = "",
    val quantization_level: String = ""
) {
    /**
     * Format size to human-readable representation.
     */
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
    
    /**
     * Format timestamp to readable date string.
     */
    fun getFormattedDate(): String {
        val instant = Instant.fromEpochSeconds(modified)
        return instant.toString()
    }
}

/**
 * Message for chat conversations.
 */
@Serializable
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = Clock.System.now().epochSeconds
)

/**
 * Response from a chat API call.
 */
@Serializable
data class ChatResponse(
    val content: String,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)
