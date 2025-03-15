# LangChain Kotlin Multiplatform Bindings

This project provides Kotlin Multiplatform bindings for the langchain-rust library using UniFFI.

## Project Structure

- `src/` - The Rust source code
  - `ffi.rs` - FFI layer for exposing Rust functions to UniFFI
  - `langchain.udl` - UniFFI definition file
- `langchain-kmp/` - Kotlin Multiplatform project
- `scripts/` - Helper scripts
  - `generate-kmp-bindings.sh` - Script to generate Kotlin bindings

## Building the Project

To build the entire project, run:

```bash
./build-all.sh
```

This will:
1. Clean the project
2. Build the Rust library
3. Generate the Kotlin bindings
4. Build the Kotlin Multiplatform library

To also publish to Maven Local, run:

```bash
./build-all.sh --publish
```

## Developing the Bindings

### Incremental Approach

The recommended approach for developing these bindings is:

1. Start with minimal UDL definitions
2. Build and test that they work
3. Gradually add more functionality

### Adding New Features

1. Update the UDL file (`src/langchain.udl`)
2. Implement the corresponding Rust functions in `src/ffi.rs`
3. Regenerate the bindings using `./scripts/generate-kmp-bindings.sh`
4. Build and test the new functionality

## Troubleshooting

### Common Issues

1. **UDL Parse Errors**
   - Make sure the UDL syntax is correct
   - Check that dictionaries and interfaces are properly terminated with semicolons

2. **Library Loading Issues**
   - Check that the library is built with the correct name
   - Verify that it's copied to the right location for each platform

3. **Binding Generation Fails**
   - Ensure you have uniffi-bindgen-cli installed
   - Try with simpler UDL definitions first

## License

This project is licensed under the MIT License.
