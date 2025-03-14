# Kotlin Multiplatform Binding Changes

## Summary of Fixes Implemented

1. **Rust Library Configuration**
   - Added `crate-type = ["cdylib", "rlib"]` to Cargo.toml to ensure proper dynamic library compilation
   - Added proper library name configuration in lib.rs with `uniffi_export_langchain!()` macro
   - Updated the ffi.rs module to include the `uniffi::include_scaffolding!("langchain")` directive

2. **Kotlin Multiplatform Project Configuration**
   - Updated build.gradle.kts to include native library linking
   - Added proper Kotlin/Native target configuration
   - Created settings.gradle.kts with repository configuration
   - Added gradle.properties with necessary KMP settings

3. **Binding Generation Process**
   - Updated generate-kmp-bindings.sh to:
     - Build the Rust project in release mode
     - Copy the generated dynamic libraries to the KMP resources directory
     - Create proper directory structure for the bindings
     - Generate documentation

4. **Helper Classes and Documentation**
   - Created LangChainSample.kt helper class to simplify using the generated bindings
   - Added better parameter naming to match Kotlin naming conventions
   - Updated documentation in the sample code

5. **Build Process Streamlining**
   - Added a build-kmp.sh script that runs the entire process in one step

## What Changed and Why

### Library Output Name

We ensured the Rust library is compiled with the right name (`langchain_rust`) by:
- Setting the correct library name in Cargo.toml
- Using the proper crate-type settings for dynamic library generation

### Native Library Linking

We fixed the KMP build.gradle.kts to correctly:
- Link the native libraries with the right names
- Use platform-specific library naming conventions (lib*.dylib, lib*.so, *.dll)
- Create a build task dependency to ensure the Rust library is built first

### Type-Safe API

The helper class uses Kotlin's strong typing and coroutines to provide:
- Thread-safe access to the FFI layer
- Proper error handling with exceptions
- Clean API with idiomatic Kotlin patterns

## How to Use

Run the integrated build script:
```bash
./build-kmp.sh
```

This will:
1. Build the Rust library with UniFFI integration
2. Generate the Kotlin bindings
3. Build the KMP library for all platforms

The resulting library will be available in the `langchain-kmp/build/outputs/` directory.
