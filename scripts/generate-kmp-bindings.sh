#!/bin/bash
set -e

# Add cargo bin to PATH
export PATH="$HOME/.cargo/bin:$PATH"

# Define paths
RUST_PROJECT_ROOT=$(pwd)
UDL_FILE="$RUST_PROJECT_ROOT/src/langchain.udl"
KMP_PROJECT="$RUST_PROJECT_ROOT/langchain-kmp"
OUTPUT_DIR="$KMP_PROJECT/src/commonMain/kotlin"

# Install uniffi_bindgen from source
echo "Installing uniffi-bindgen from source..."
git clone https://github.com/mozilla/uniffi-rs.git
cd uniffi-rs
cargo build -p uniffi_bindgen
cd ..

# Build Rust project
echo "Building Rust project..."
cargo build --release

# Create directories for KMP bindings
mkdir -p "$OUTPUT_DIR"
mkdir -p "$KMP_PROJECT/src/jvmMain/resources"
mkdir -p "$KMP_PROJECT/src/androidMain/jniLibs/arm64-v8a"
mkdir -p "$KMP_PROJECT/src/androidMain/jniLibs/armeabi-v7a"
mkdir -p "$KMP_PROJECT/src/androidMain/jniLibs/x86"
mkdir -p "$KMP_PROJECT/src/androidMain/jniLibs/x86_64"

# Generate Kotlin bindings
echo "Generating Kotlin bindings..."
./uniffi-rs/target/debug/uniffi-bindgen generate "$UDL_FILE" --language kotlin --out-dir "$OUTPUT_DIR"

# Detect OS and architecture
OS="$(uname)"
ARCH="$(uname -m)"

# Copy the dynamic library to the appropriate location
echo "Copying dynamic libraries to platform-specific directories..."

if [[ "$OS" == "Darwin" ]]; then
    DYLIB_PATH="$RUST_PROJECT_ROOT/target/release/liblangchain_rust.dylib"
    if [ -f "$DYLIB_PATH" ]; then
        cp "$DYLIB_PATH" "$KMP_PROJECT/src/jvmMain/resources/"
        
        # Also copy for Android (in a real project, you'd cross-compile for Android)
        cp "$DYLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/arm64-v8a/liblangchain_rust.so"
        cp "$DYLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/armeabi-v7a/liblangchain_rust.so"
        cp "$DYLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/x86_64/liblangchain_rust.so"
        cp "$DYLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/x86/liblangchain_rust.so"
        
        echo "Copied macOS dylib to resources"
    else
        echo "Warning: Could not find $DYLIB_PATH"
    fi
elif [[ "$OS" == "Linux" ]]; then
    SOLIB_PATH="$RUST_PROJECT_ROOT/target/release/liblangchain_rust.so"
    if [ -f "$SOLIB_PATH" ]; then
        cp "$SOLIB_PATH" "$KMP_PROJECT/src/jvmMain/resources/"
        
        # Also copy for Android (in a real project, you'd cross-compile for Android)
        cp "$SOLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/arm64-v8a/liblangchain_rust.so"
        cp "$SOLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/armeabi-v7a/liblangchain_rust.so"
        cp "$SOLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/x86_64/liblangchain_rust.so"
        cp "$SOLIB_PATH" "$KMP_PROJECT/src/androidMain/jniLibs/x86/liblangchain_rust.so"
        
        echo "Copied Linux .so to resources"
    else
        echo "Warning: Could not find $SOLIB_PATH"
    fi
elif [[ "$OS" == *"MINGW"* ]] || [[ "$OS" == *"MSYS"* ]] || [[ "$OS" == *"CYGWIN"* ]]; then
    DLL_PATH="$RUST_PROJECT_ROOT/target/release/langchain_rust.dll"
    if [ -f "$DLL_PATH" ]; then
        cp "$DLL_PATH" "$KMP_PROJECT/src/jvmMain/resources/"
        echo "Copied Windows .dll to resources"
    else
        echo "Warning: Could not find $DLL_PATH"
    fi
else
    echo "Warning: Unsupported OS: $OS. Manual library copying may be required."
fi

# Create a jniLibs.version file to track which native libraries are included
echo "Creating version tracking for native libraries..."
echo "rust_version=\"$(cargo --version)\"" > "$KMP_PROJECT/src/jvmMain/resources/jniLibs.version"
echo "build_date=\"$(date)\"" >> "$KMP_PROJECT/src/jvmMain/resources/jniLibs.version"
echo "os=\"$OS\"" >> "$KMP_PROJECT/src/jvmMain/resources/jniLibs.version"
echo "arch=\"$ARCH\"" >> "$KMP_PROJECT/src/jvmMain/resources/jniLibs.version"

echo "Bindings generated successfully!"
echo "You can now build the Kotlin Multiplatform project in $KMP_PROJECT"
