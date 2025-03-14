package ai.langchain.sample

import ai.langchain.kmp.LangChainSample
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("LangChain Kotlin Bindings Demo")
    println("==============================")
    
    try {
        val langChain = LangChainSample()
        
        // Display version
        println("LangChain version: ${langChain.getVersion()}")
        
        // Simple LLM invocation
        println("\nSimple LLM invocation:")
        val response = langChain.simpleLlmInvocation("What is LangChain?")
        println("Response: $response")
        
        // Chat with LLM
        println("\nChat with LLM:")
        val llm = langChain.createLlm()
        val result = langChain.chatWithLlm(
            llm,
            "You are a helpful assistant.",
            "Tell me about Kotlin Multiplatform and Rust interoperability."
        )
        println("Text: ${result.text}")
        println("Tokens: ${result.totalTokens ?: "unknown"}")
        
        // Generate embeddings
        println("\nGenerating embeddings:")
        val embedder = langChain.createEmbedder()
        val embedding = langChain.createEmbedding(embedder, "This is a test sentence.")
        println("Embedding dimension: ${embedding.size}")
        println("First 5 values: ${embedding.take(5)}")
        
        // Using a chain
        println("\nUsing LLM Chain:")
        val chain = langChain.createLlmChain(
            "You are a friendly assistant. Answer the following: {input}",
            llm
        )
        val chainResult = langChain.runChain(chain, "How does UniFFI work?")
        println("Chain result: $chainResult")
        
        // Conversation with context
        println("\nConversation with context:")
        val context = langChain.createConversationContext(
            userMessages = listOf(
                "My name is Alice.",
                "What can you tell me about Rust?"
            ),
            aiMessages = listOf(
                "Hello Alice! How can I help you today?"
            )
        )
        
        val contextResult = langChain.runChainWithContext(
            chain,
            "Can you recommend some resources for learning more?",
            context
        )
        println("Result with context: $contextResult")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}
