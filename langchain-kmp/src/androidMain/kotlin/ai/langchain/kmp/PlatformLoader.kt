package ai.langchain.kmp

import java.io.File
import java.io.FileOutputStream

/**
 * Platform-specific loader for native libraries on Android.
 */
internal actual object PlatformLoader {
    /**
     * Load the langchain_rust native library on Android.
     * On Android, the library should be packaged as part of the APK.
     */
    actual fun loadLibrary() {
        try {
            // On Android, the native libraries are packaged in the APK
            // and should be loaded by name without path or extension
            System.loadLibrary("langchain_rust")
            println("Loaded library from Android APK")
        } catch (e: UnsatisfiedLinkError) {
            // For development, try to load from file system
            val libPaths = listOf(
                "/data/local/tmp/liblangchain_rust.so",
                "${System.getProperty("user.dir")}/liblangchain_rust.so"
            )
            
            var loaded = false
            for (path in libPaths) {
                try {
                    val file = File(path)
                    if (file.exists()) {
                        System.load(file.absolutePath)
                        println("Loaded library from: $path")
                        loaded = true
                        break
                    }
                } catch (e2: Exception) {
                    // Continue to next path
                }
            }
            
            if (!loaded) {
                throw UnsatisfiedLinkError(
                    "Failed to load native library 'langchain_rust'. Make sure it's included in your project. Error: ${e.message}"
                )
            }
        }
    }
}
