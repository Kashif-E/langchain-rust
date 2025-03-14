#!/bin/bash
set -e

# Check if Gobley is installed
if ! command -v gobley &> /dev/null; then
    echo "Gobley not found. Installing Gobley..."
    cargo install --git https://github.com/gobley/gobley
fi

# Define paths
RUST_PROJECT_ROOT=$(pwd)
UDL_FILE="$RUST_PROJECT_ROOT/src/langchain.udl"
KMP_PROJECT="$RUST_PROJECT_ROOT/langchain-kmp"
OUTPUT_DIR="$KMP_PROJECT/src"

# Ensure Rust project is built with UniFFI integration
echo "Building Rust project..."
cargo build --release

# Create directories for KMP bindings
mkdir -p "$OUTPUT_DIR/commonMain/kotlin/ai/langchain/kmp"
mkdir -p "$OUTPUT_DIR/jvmMain/kotlin/ai/langchain/kmp"
mkdir -p "$OUTPUT_DIR/nativeMain/kotlin/ai/langchain/kmp"

# Generate Kotlin bindings using Gobley
echo "Generating Kotlin bindings with Gobley..."
gobley generate \
    --udl "$UDL_FILE" \
    --out "$OUTPUT_DIR" \
    --package "ai.langchain.kmp" \
    --language kotlin

# Copy the dynamic library to the appropriate location
echo "Copying dynamic libraries..."
mkdir -p "$KMP_PROJECT/src/nativeMain/resources"
cp "$RUST_PROJECT_ROOT/target/release/liblangchain_rust.dylib" "$KMP_PROJECT/src/nativeMain/resources/" 2>/dev/null || :
cp "$RUST_PROJECT_ROOT/target/release/liblangchain_rust.so" "$KMP_PROJECT/src/nativeMain/resources/" 2>/dev/null || :
cp "$RUST_PROJECT_ROOT/target/release/langchain_rust.dll" "$KMP_PROJECT/src/nativeMain/resources/" 2>/dev/null || :

# Create a simple README with usage instructions
cat > "$KMP_PROJECT/README.md" << EOF
# LangChain Kotlin Multiplatform Bindings

This package provides Kotlin Multiplatform bindings for the langchain-rust library.

## Usage

```kotlin
import ai.langchain.kmp.LangChainSample
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val langChain = LangChainSample()
    
    // Get library version
    println("LangChain version: \${langChain.getVersion()}")
    
    // Simple LLM call
    val response = langChain.simpleLlmInvocation("What is LangChain?")
    println("Response: \$response")
}
```

## Platforms Supported

- JVM
- iOS
- macOS
- Linux
- Windows

## Requirements

Make sure to set the appropriate API keys for the LLM provider you're using (e.g., OpenAI).
EOF

echo "Bindings generated successfully!"
echo "You can now build the Kotlin Multiplatform project in $KMP_PROJECT"
