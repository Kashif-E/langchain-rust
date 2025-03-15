package ai.langchain.kmp

import java.io.File
import java.io.FileOutputStream

/**
 * Platform-specific loader for native libraries on JVM.
 */
internal actual object PlatformLoader {
    /**
     * Load the langchain_rust native library on JVM.
     * This extracts the library from resources if necessary.
     */
    actual fun loadLibrary() {
        try {
            // Try to load directly (works if library is in the path)
            System.loadLibrary("langchain_rust")
            println("Loaded library from system library path")
            return
        } catch (e: UnsatisfiedLinkError) {
            // Fallback to extracting from resources
            val osName = System.getProperty("os.name").lowercase()
            val libraryName = when {
                osName.contains("mac") -> "liblangchain_rust.dylib"
                osName.contains("linux") -> "liblangchain_rust.so"
                osName.contains("windows") -> "langchain_rust.dll"
                else -> throw UnsupportedOperationException("Unsupported platform: $osName")
            }
            
            extractAndLoadLibrary(libraryName)
        }
    }
    
    private fun extractAndLoadLibrary(libraryName: String) {
        val classLoader = PlatformLoader::class.java.classLoader
        val resourceStream = classLoader.getResourceAsStream(libraryName)
        
        if (resourceStream != null) {
            val tempFile = File.createTempFile("langchain_rust", libraryName.substringAfterLast('.'))
            tempFile.deleteOnExit()
            
            FileOutputStream(tempFile).use { outputStream ->
                resourceStream.copyTo(outputStream)
            }
            
            System.load(tempFile.absolutePath)
            println("Loaded library from resources: $libraryName")
        } else {
            // Try development paths
            val devPaths = listOf(
                System.getProperty("user.dir") + "/target/release/$libraryName",
                "../target/release/$libraryName"
            )
            
            for (path in devPaths) {
                val file = File(path)
                if (file.exists()) {
                    System.load(file.absolutePath)
                    println("Loaded library from development path: $path")
                    return
                }
            }
            
            throw UnsatisfiedLinkError(
                "Failed to load native library $libraryName. Make sure it's included in your project."
            )
        }
    }
}
