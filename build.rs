fn main() {
    // Generate UniFFI scaffolding from UDL file
    uniffi::generate_scaffolding("src/langchain.udl").unwrap();
}
