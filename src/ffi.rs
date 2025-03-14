use crate::language_models::llm::LLM as RustLLM;
use crate::llm::openai::{OpenAI, OpenAIModel};
use crate::embedding::openai::OpenAIEmbeddings;
use crate::chain::{Chain as RustChain, LLMChainBuilder};
use crate::schemas::messages::Message as RustMessage;
use crate::schemas::messages::MessageType;
use crate::prompt::{PromptTemplate, HumanMessagePromptTemplate};
use std::collections::HashMap;
use std::sync::Arc;
use thiserror::Error;

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

impl From<crate::language_models::LLMError> for LangChainError {
    fn from(err: crate::language_models::LLMError) -> Self {
        match err {
            crate::language_models::LLMError::RequestError(e) => {
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
            crate::language_models::LLMError::DeserializationError(e) => {
                LangChainError::ApiError(e.to_string())
            }
            crate::language_models::LLMError::InvalidArgument(e) => {
                LangChainError::InvalidArgument(e)
            }
            crate::language_models::LLMError::ContentNotFound(e) => {
                LangChainError::ApiError(e)
            }
            _ => LangChainError::UnknownError(format!("{:?}", err)),
        }
    }
}

#[derive(Debug, Clone)]
pub struct Message {
    pub content: String,
    pub message_type: String,
}

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

#[derive(Debug, Clone)]
pub struct GenerationResult {
    pub text: String,
    pub prompt_tokens: Option<u32>,
    pub completion_tokens: Option<u32>,
    pub total_tokens: Option<u32>,
}

#[derive(Debug, Clone)]
pub struct LlmOptions {
    pub temperature: Option<f64>,
    pub max_tokens: Option<u32>,
    pub stop_words: Option<Vec<String>>,
}

pub struct Llm {
    model: Arc<dyn RustLLM + Send + Sync>,
}

impl Llm {
    pub fn new(model_name: String) -> Self {
        // Create the appropriate LLM based on the model name
        let model: Arc<dyn RustLLM + Send + Sync> = if model_name.contains("gpt") {
            Arc::new(OpenAI::default().with_model(model_name))
        } else {
            // Default to GPT-4o-mini if model isn't recognized
            Arc::new(OpenAI::default().with_model(OpenAIModel::Gpt4oMini.to_string()))
        };
        
        Self { model }
    }
    
    pub fn invoke(&self, prompt: String) -> Result<String, LangChainError> {
        let rt = tokio::runtime::Runtime::new().unwrap();
        rt.block_on(self.model.invoke(&prompt)).map_err(|e| e.into())
    }
    
    pub fn generate(&self, messages: Vec<Message>, options: Option<LlmOptions>) -> Result<GenerationResult, LangChainError> {
        let rust_messages: Vec<RustMessage> = messages.into_iter().map(|m| m.into()).collect();
        
        let rt = tokio::runtime::Runtime::new().unwrap();
        
        // Apply options if provided
        let model = if let Some(opts) = options {
            let mut model_clone = self.model.clone();
            let mut call_options = crate::language_models::options::CallOptions::default();
            
            if let Some(temp) = opts.temperature {
                call_options.temperature = Some(temp);
            }
            
            if let Some(max) = opts.max_tokens {
                call_options.max_tokens = Some(max as i32);
            }
            
            if let Some(stop) = opts.stop_words {
                call_options.stop_words = Some(stop);
            }
            
            Arc::new(model_clone)
        } else {
            self.model.clone()
        };
        
        let result = rt.block_on(model.generate(&rust_messages))?;
        
        Ok(GenerationResult {
            text: result.generation,
            prompt_tokens: result.tokens.as_ref().map(|t| t.prompt_tokens as u32),
            completion_tokens: result.tokens.as_ref().map(|t| t.completion_tokens as u32),
            total_tokens: result.tokens.as_ref().map(|t| t.total_tokens as u32),
        })
    }
}

pub struct Embedder {
    model_name: String,
}

impl Embedder {
    pub fn new(model_name: String) -> Self {
        Self { model_name }
    }
    
    pub fn embed_query(&self, text: String) -> Result<Vec<f32>, LangChainError> {
        let embeddings = OpenAIEmbeddings::default();
        let rt = tokio::runtime::Runtime::new().unwrap();
        
        rt.block_on(embeddings.embed_query(&text))
            .map_err(|e| LangChainError::ApiError(e.to_string()))
    }
    
    pub fn embed_documents(&self, texts: Vec<String>) -> Result<Vec<Vec<f32>>, LangChainError> {
        let embeddings = OpenAIEmbeddings::default();
        let rt = tokio::runtime::Runtime::new().unwrap();
        
        rt.block_on(embeddings.embed_documents(&texts))
            .map_err(|e| LangChainError::ApiError(e.to_string()))
    }
}

pub struct Chain {
    llm: Llm,
    prompt_template: String,
}

impl Chain {
    pub fn new_llm_chain(prompt_template: String, llm: Llm) -> Self {
        Self {
            llm,
            prompt_template,
        }
    }
    
    pub fn run(&self, input: String) -> Result<String, LangChainError> {
        // Create a simple LLM chain
        let template = HumanMessagePromptTemplate::new(PromptTemplate::new(
            self.prompt_template.clone(),
            vec!["input".to_string()],
        ));
        
        let mut values = HashMap::new();
        values.insert("input".to_string(), input);
        
        let formatted = template.format(&values)
            .map_err(|e| LangChainError::InvalidArgument(e.to_string()))?;
        
        let message: RustMessage = formatted;
        
        self.llm.generate(vec![message.into()], None)
            .map(|res| res.text)
    }
    
    pub fn run_with_context(&self, input: String, context: Vec<Message>) -> Result<String, LangChainError> {
        // Similar to run but includes the context
        let mut rust_context: Vec<RustMessage> = context.into_iter().map(|m| m.into()).collect();
        
        // Add the input as the last message
        rust_context.push(RustMessage::new_human_message(&input));
        
        self.llm.generate(rust_context.into_iter().map(|m| m.into()).collect(), None)
            .map(|res| res.text)
    }
}

// Top-level functions
pub fn invoke_llm(model_name: String, prompt: String) -> Result<String, LangChainError> {
    let llm = Llm::new(model_name);
    llm.invoke(prompt)
}

pub fn get_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

// Include the uniffi scaffolding that was generated from our *.udl file
uniffi::include_scaffolding!("langchain");
