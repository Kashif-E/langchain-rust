#!/bin/bash
set -e

echo "Building LangChain Kotlin Multiplatform Bindings"
echo "==============================================="

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
