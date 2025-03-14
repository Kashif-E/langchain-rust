package ai.langchain.kmp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A helper class that demonstrates how to use the langchain-rust bindings.
 */
class LangChainSample {
    /**
     * Gets the version of the underlying langchain-rust library.
     */
    fun getVersion(): String {
        return langchain.getVersion()
    }
    
    /**
     * Simple example of invoking an LLM with a prompt.
     * 
     * @param prompt The text prompt to send to the LLM
     * @param modelName The name of the model to use (e.g., "gpt-4o-mini")
     * @return The generated response
     */
    suspend fun simpleLlmInvocation(prompt: String, modelName: String = "gpt-4o-mini"): String {
        return withContext(Dispatchers.Default) {
            try {
                langchain.invokeLlm(modelName, prompt)
            } catch (e: LangChainError) {
                "Error: ${e.message}"
            }
        }
    }
    
    /**
     * Creates an LLM instance that can be reused for multiple calls.
     * 
     * @param modelName The name of the model to use
     * @return An LLM instance
     */
    fun createLlm(modelName: String = "gpt-4o-mini"): Llm {
        return Llm(modelName)
    }
    
    /**
     * Create a chat with messages of different types.
     * 
     * @param llm The LLM instance to use
     * @param systemPrompt The system prompt to set context
     * @param userMessage The user's message
     * @return The generated response
     */
    suspend fun chatWithLlm(
        llm: Llm,
        systemPrompt: String, 
        userMessage: String
    ): GenerationResult {
        return withContext(Dispatchers.Default) {
            val messages = listOf(
                Message(content = systemPrompt, messageType = "system"),
                Message(content = userMessage, messageType = "human")
            )
            
            val options = LlmOptions(
                temperature = 0.7,
                maxTokens = 500,
                stopWords = listOf("STOP", "END")
            )
            
            llm.generate(messages, options)
        }
    }
    
    /**
     * Creates an embedder for generating vector embeddings.
     * 
     * @param modelName The embedding model to use
     * @return An Embedder instance
     */
    fun createEmbedder(modelName: String = "text-embedding-3-small"): Embedder {
        return Embedder(modelName)
    }
    
    /**
     * Generate embeddings for a single text input.
     * 
     * @param embedder The embedder instance
     * @param text The text to embed
     * @return A vector of floating-point values representing the embedding
     */
    suspend fun createEmbedding(embedder: Embedder, text: String): List<Float> {
        return withContext(Dispatchers.Default) {
            try {
                embedder.embedQuery(text)
            } catch (e: LangChainError) {
                emptyList()
            }
        }
    }
    
    /**
     * Generate embeddings for multiple texts in batch.
     * 
     * @param embedder The embedder instance
     * @param texts List of texts to embed
     * @return A list of embedding vectors
     */
    suspend fun createEmbeddings(embedder: Embedder, texts: List<String>): List<List<Float>> {
        return withContext(Dispatchers.Default) {
            try {
                embedder.embedDocuments(texts)
            } catch (e: LangChainError) {
                emptyList()
            }
        }
    }
    
    /**
     * Creates a simple LLM Chain that can process inputs through a prompt template.
     * 
     * @param promptTemplate The template string with {input} placeholder
     * @param llm The LLM instance to use
     * @return A Chain instance
     */
    fun createLlmChain(promptTemplate: String, llm: Llm): Chain {
        return Chain.newLlmChain(promptTemplate, llm)
    }
    
    /**
     * Run a chain with a simple input.
     * 
     * @param chain The chain instance
     * @param input The input to process
     * @return The generated output
     */
    suspend fun runChain(chain: Chain, input: String): String {
        return withContext(Dispatchers.Default) {
            try {
                chain.run(input)
            } catch (e: LangChainError) {
                "Error: ${e.message}"
            }
        }
    }
    
    /**
     * Run a chain with input and conversation context.
     * 
     * @param chain The chain instance
     * @param input The input to process
     * @param context Previous conversation messages for context
     * @return The generated output
     */
    suspend fun runChainWithContext(
        chain: Chain, 
        input: String,
        context: List<Message>
    ): String {
        return withContext(Dispatchers.Default) {
            try {
                chain.runWithContext(input, context)
            } catch (e: LangChainError) {
                "Error: ${e.message}"
            }
        }
    }
    
    /**
     * Helper function to create a conversation context.
     * 
     * @param userMessages List of user messages
     * @param aiMessages List of AI responses
     * @return A list of alternating messages ready to use as context
     */
    fun createConversationContext(
        userMessages: List<String>,
        aiMessages: List<String>
    ): List<Message> {
        require(aiMessages.size == userMessages.size || aiMessages.size == userMessages.size - 1) {
            "AI messages must equal user messages or be one less"
        }
        
        val messages = mutableListOf<Message>()
        
        for (i in userMessages.indices) {
            messages.add(Message(content = userMessages[i], messageType = "human"))
            if (i < aiMessages.size) {
                messages.add(Message(content = aiMessages[i], messageType = "ai"))
            }
        }
        
        return messages
    }
}
