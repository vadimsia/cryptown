pub mod processor;
mod instruction;
mod error;

#[cfg(not(feature = "no-entrypoint"))]
mod entrypoint;