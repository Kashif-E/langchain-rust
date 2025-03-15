@file:Suppress("RemoveRedundantBackticks")

package uniffi.langchain

/**
 * Get the version of the langchain-rust library.
 * @return The version string.
 */
expect fun get_version(): String

/**
 * Invoke an LLM with a simple prompt.
 * @param model_name The name of the model to use.
 * @param prompt The prompt to send to the LLM.
 * @return The generated text.
 * @throws LangChainException if an error occurs.
 */
expect fun invoke_llm(model_name: String, prompt: String): String
