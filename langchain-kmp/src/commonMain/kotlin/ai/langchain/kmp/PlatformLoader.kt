package ai.langchain.kmp

/**
 * Platform-specific loader for native libraries.
 * This is implemented differently for each platform.
 */
internal expect object PlatformLoader {
    /**
     * Load the langchain_rust native library.
     * Each platform implements this differently.
     */
    fun loadLibrary()
}

/**
 * Initialize the native library when the class is loaded.
 */
internal object LibraryInitializer {
    init {
        PlatformLoader.loadLibrary()
    }
}
