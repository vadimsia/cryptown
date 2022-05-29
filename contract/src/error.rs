use thiserror::Error;

use solana_program::program_error::ProgramError;

#[derive(Error, Debug, Clone, Copy)]
pub enum ChunkError {
    #[error("Invalid Instruction")]
    InvalidInstruction,
}

impl From<ChunkError> for ProgramError {
    fn from(e: ChunkError) -> Self {
        ProgramError::Custom(e as u32)
    }
}