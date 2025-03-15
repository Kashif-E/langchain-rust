use crate::language_models::llm::LLM as RustLLM;
use crate::language_models::LLMError;
use crate::llm::openai::OpenAI;
use crate::schemas::messages::Message as RustMessage;
use crate::schemas::messages::MessageType;
use thiserror::Error;
use once_cell::sync::Lazy;
use tokio::runtime::Runtime;

/// Global singleton Tokio runtime for efficient FFI calls
static RUNTIME: Lazy<Runtime> = Lazy::new(|| {
    Runtime::new().expect("Failed to create Tokio runtime")
});

/// Error types for LangChain bindings
#[derive(Error, Debug)]
pub enum LangChainError {
    #[error("Invalid model specified")]
    InvalidModel,
    
    #[error("API error occurred: {0}")]
    ApiError(String),
    
    #[error("Network error occurred: {0}")]
    NetworkError(String),
    
    #[error("Invalid argument: {0}")]
    InvalidArgument(String),
    
    #[error("Token limit exceeded")]
    TokenLimitExceeded,
    
    #[error("Unauthorized, check your API keys")]
    Unauthorized,
    
    #[error("Server error: {0}")]
    ServerError(String),
    
    #[error("Unknown error: {0}")]
    UnknownError(String),
}

/// Convert internal LLM errors to FFI-friendly LangChainError
impl From<LLMError> for LangChainError {
    fn from(err: LLMError) -> Self {
        match err {
            LLMError::RequestError(e) => {
                if e.to_string().contains("401") {
                    LangChainError::Unauthorized
                } else if e.to_string().contains("429") {
                    LangChainError::TokenLimitExceeded
                } else if e.to_string().contains("5") {
                    LangChainError::ServerError(e.to_string())
                } else {
                    LangChainError::ApiError(e.to_string())
                }
            }
            LLMError::SerdeError(e) => {
                LangChainError::ApiError(e.to_string())
            }
            LLMError::ContentNotFound(e) => {
                LangChainError::ApiError(e)
            }
            _ => LangChainError::UnknownError(format!("{:?}", err)),
        }
    }
}

/// Message structure for FFI
#[derive(Debug, Clone)]
pub struct Message {
    pub content: String,
    pub message_type: String,
}

/// Convert FFI Message to internal Rust Message
impl From<Message> for RustMessage {
    fn from(msg: Message) -> Self {
        match msg.message_type.as_str() {
            "human" => RustMessage::new_human_message(&msg.content),
            "ai" => RustMessage::new_ai_message(&msg.content),
            "system" => RustMessage::new_system_message(&msg.content),
            _ => RustMessage::new_human_message(&msg.content),
        }
    }
}

/// Convert internal Rust Message to FFI Message
impl From<RustMessage> for Message {
    fn from(msg: RustMessage) -> Self {
        let message_type = match msg.message_type {
            MessageType::HumanMessage => "human".to_string(),
            MessageType::AIMessage => "ai".to_string(),
            MessageType::SystemMessage => "system".to_string(),
            MessageType::ToolMessage => "tool".to_string(),
        };
        
        Self {
            content: msg.content,
            message_type,
        }
    }
}

/// Get the library version
/// 
/// Returns the version string of the langchain-rust library
pub fn get_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

/// Invoke a language model with a prompt
/// 
/// @param model_name The name of the model to use
/// @param prompt The prompt text to send to the model
/// @return The generated text response
/// @throws LangChainError if the invocation fails
pub fn invoke_llm(model_name: String, prompt: String) -> Result<String, LangChainError> {
    let model = OpenAI::default().with_model(model_name);
    RUNTIME.block_on(model.invoke(&prompt)).map_err(|e| e.into())
}

// Include the uniffi scaffolding that was generated from our *.udl file
uniffi::include_scaffolding!("langchain");
