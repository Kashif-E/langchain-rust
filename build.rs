fn main() {
    // Generate UniFFI scaffolding from UDL file
    uniffi_build::generate_scaffolding("src/langchain.udl").unwrap();
}
