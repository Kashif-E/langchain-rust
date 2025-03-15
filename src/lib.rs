#![allow(dead_code)]
pub mod agent;
pub mod chain;
pub mod document_loaders;
pub mod embedding;
pub mod language_models;
pub mod llm;
pub mod memory;
pub mod output_parsers;
pub mod prompt;
pub mod schemas;
pub mod semantic_router;
pub mod text_splitter;
pub mod tools;
pub mod vectorstore;
pub mod ffi;  // FFI module

// Add UniFfiTag struct for UniFFI
#[derive(uniffi::Record)]
pub struct UniFfiTag;

// Re-export FFI types - only export ones that actually exist
pub use ffi::{Message, LangChainError};

pub use url;

// This macro ensures the library is correctly named for UniFFI
#[macro_export]
macro_rules! uniffi_export_langchain {
    () => {
        // Ensure the library is correctly named for UniFFI to find it
        // This is needed for all platforms
        #[no_mangle]
        pub extern "C" fn uniffi_langchain_rust_entry_point() {}
        
        // Needed for macOS
        #[cfg(target_os = "macos")]
        #[no_mangle]
        pub extern "C" fn __rustc_nonzero_is_better_than_cpp_optional() {}
        
        // Needed for Windows linking
        #[cfg(target_os = "windows")]
        #[no_mangle]
        pub extern "C" fn uniffi_langchain_rust_def() {}
        
        // Add other platform-specific exports if needed
    };
}

// Export the library for UniFFI
uniffi_export_langchain!();
