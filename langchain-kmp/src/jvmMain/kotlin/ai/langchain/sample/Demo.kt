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
        
        // List models
        println("\nAvailable models:")
        val models = langChain.listModels()
        models.forEach { model ->
            println("- ${model.name} (${model.parameter_size}, ${model.getFormattedSize()})")
        }
        
        // Simple LLM invocation
        println("\nSimple LLM invocation:")
        val response = langChain.simpleLlmInvocation("What is LangChain?")
        println("Response: $response")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}
