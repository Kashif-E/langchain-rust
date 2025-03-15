#!/bin/bash
set -e

# Step 1: Clean the project
echo "Cleaning project..."
cargo clean

# Step 2: Build the Rust project
echo "Building Rust project..."
cargo build --release

# Step 3: Generate the bindings
echo "Generating bindings..."
./scripts/generate-kmp-bindings.sh

# Step 4: Build the Kotlin Multiplatform library
echo "Building KMP library..."
cd langchain-kmp && ./gradlew build

# Step 5: Publish to Maven Local (optional)
if [ "$1" == "--publish" ]; then
    echo "Publishing to Maven Local..."
    cd langchain-kmp && ./gradlew publishToMavenLocal
fi

echo "Build completed successfully!"
