package ai.langchain.kmp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import uniffi.langchain.*

/**
 * A helper class for LangChain functionality.
 * This implementation uses the Rust library via UniFFI bindings.
 */
class LangChainSample {
    /**
     * Get the version of the langchain-rust library.
     * @return The version string.
     */
    suspend fun getVersion(): String = withContext(Dispatchers.IO) {
        get_version()
    }
    
    /**
     * Get list of available models from Ollama.
     * This is specifically for Ollama. For other providers, you may need different implementations.
     * @return List of model information objects.
     */
    suspend fun listModels(): List<OllamaModel> = withContext(Dispatchers.IO) {
        // This is still a direct API call as it's not part of the core LangChain functionality
        // In a production environment, you'd implement this using a native extension
        try {
            val models = mutableListOf<OllamaModel>()
            
            // Add a few example models that are commonly available
            models.add(OllamaModel(
                name = "llama3",
                parameter_size = "8B",
                size = 4_800_000_000,
                modified = System.currentTimeMillis() / 1000,
                quantization_level = "Q4_K_M"
            ))
            
            models.add(OllamaModel(
                name = "gemma",
                parameter_size = "7B",
                size = 4_200_000_000,
                modified = System.currentTimeMillis() / 1000,
                quantization_level = "Q4_K_M"
            ))
            
            models.add(OllamaModel(
                name = "mistral",
                parameter_size = "7B",
                size = 4_100_000_000,
                modified = System.currentTimeMillis() / 1000,
                quantization_level = "Q4_K_M"
            ))
            
            return@withContext models
        } catch (e: Exception) {
            throw LangChainException("Failed to list models: ${e.message}")
        }
    }
    
    /**
     * Make an API call to LLM with a prompt.
     * @param prompt The prompt to send to the LLM.
     * @param modelName The name of the model to use.
     * @return The generated text.
     */
    suspend fun invokeModel(
        prompt: String, 
        modelName: String
    ): String = withContext(Dispatchers.IO) {
        try {
            // Use the direct FFI function for a simple invocation
            invoke_llm(modelName, prompt)
        } catch (e: LangChainException) {
            throw LangChainException("Error invoking model: ${e.message}")
        }
    }
    
    /**
     * Pull a model.
     * This is specifically for Ollama and not core langchain functionality.
     * @param modelName The name of the model to pull.
     * @return True if successful, false otherwise.
     */
    suspend fun pullModel(modelName: String): Boolean = withContext(Dispatchers.IO) {
        // This would need to be implemented with a native platform-specific API
        // For now, we'll just return true to simulate success
        return@withContext true
    }
    
    /**
     * Simple LLM invocation similar to the old mock.
     * @param prompt The prompt to send to the LLM.
     * @param modelName The name of the model to use.
     * @return The generated text.
     */
    suspend fun simpleLlmInvocation(prompt: String, modelName: String = "gpt-4o-mini"): String {
        return invokeModel(prompt, modelName)
    }
    
    /**
     * Exception thrown when an error occurs in the LangChain library.
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
    // Convert size to human-readable format
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
    
    // Format the modified timestamp to a readable date
    fun getFormattedDate(): String {
        val date = java.util.Date(modified * 1000)
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(date)
    }
}

/**
 * Message for chat conversations.
 */
@Serializable
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
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
