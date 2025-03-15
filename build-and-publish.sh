#!/bin/bash
set -e

# First, build the Rust library and generate bindings
./scripts/generate-kmp-bindings.sh

# Then build and publish the KMP library
cd langchain-kmp
./gradlew build publishToMavenLocal

echo "Library published to Maven Local repository"
