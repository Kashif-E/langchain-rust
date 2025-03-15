#!/bin/bash
set -e

echo "Building LangChain Kotlin Multiplatform Bindings"
echo "==============================================="

# Determine if this is a clean build
if [ "$1" == "clean" ]; then
    echo "Performing clean build..."
    cargo clean
    if [ -d "langchain-kmp/build" ]; then
        rm -rf langchain-kmp/build
    fi
fi

# 1. Build the Rust project
echo "Step 1: Building Rust project with UniFFI..."
cargo build --release

# 2. Generate KMP bindings
echo "Step 2: Generating Kotlin Multiplatform bindings..."
chmod +x scripts/generate-kmp-bindings.sh
./scripts/generate-kmp-bindings.sh

# 3. Build the KMP library
echo "Step 3: Building Kotlin Multiplatform library..."
cd langchain-kmp
./gradlew build

echo ""
echo "Build completed successfully!"
echo "The LangChain KMP library can be found in: $(pwd)/build/outputs/"

# Print library locations
echo ""
echo "JVM library: $(pwd)/build/libs/langchain-kmp-jvm-0.1.0.jar"
echo "Android library: $(pwd)/build/outputs/aar/langchain-kmp-release.aar"

# Check for macOS and iOS artifacts
if [[ "$(uname)" == "Darwin" ]]; then
    echo "macOS libraries:"
    echo "  - $(pwd)/build/out/macosX64/releaseFramework/langchain_kmp.framework"
    echo "  - $(pwd)/build/out/macosArm64/releaseFramework/langchain_kmp.framework"
    
    echo "iOS libraries:"
    echo "  - $(pwd)/build/out/iosX64/releaseFramework/langchain_kmp.framework"
    echo "  - $(pwd)/build/out/iosArm64/releaseFramework/langchain_kmp.framework"
    echo "  - $(pwd)/build/out/iosSimulatorArm64/releaseFramework/langchain_kmp.framework"
fi

# Check for Windows artifacts
if [[ "$(uname)" == *"MINGW"* ]] || [[ "$(uname)" == *"MSYS"* ]] || [[ "$(uname)" == *"CYGWIN"* ]]; then
    echo "Windows libraries:"
    echo "  - $(pwd)/build/out/mingwX64/releaseLib/langchain_kmp.lib"
fi

# Check for Linux artifacts
if [[ "$(uname)" == "Linux" ]]; then
    echo "Linux libraries:"
    echo "  - $(pwd)/build/out/linuxX64/releaseLib/liblangchain_kmp.so"
fi
