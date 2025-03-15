package ai.langchain.kmp

/**
 * Platform-specific loader for native libraries.
 * This handles the correct loading of the langchain_rust native library
 * on each supported platform.
 */
internal actual object PlatformLoader {
    /**
     * Load the langchain_rust native library.
     * For Kotlin/Native targets, the library is typically bundled with the binary.
     */
    actual fun loadLibrary() {
        // For Kotlin/Native, the native libraries are typically
        // included at compile time or linked as part of the build
        // and don't need explicit loading
        println("Native library support - no explicit loading required")
    }
}
