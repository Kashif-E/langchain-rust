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

// Re-export FFI types for documentation
pub use ffi::{Llm, Embedder, Chain, Message, GenerationResult, LlmOptions, LangChainError};

pub use url;

// This macro ensures the library is correctly named for UniFFI
#[macro_export]
macro_rules! uniffi_export_langchain {
    () => {
        // Ensure the library is correctly named for UniFFI to find it
        #[cfg(target_os = "macos")]
        #[no_mangle]
        pub extern "C" fn __rustc_nonzero_is_better_than_cpp_optional() {}
    };
}

// Export the library for UniFFI
uniffi_export_langchain!();
