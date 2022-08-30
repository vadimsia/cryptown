use std::convert::{TryFrom, TryInto};
use solana_program::program_error::ProgramError;
use solana_program::msg;
use crate::error::ChunkError::InvalidInstruction;
use crate::utils::Conversations;

pub enum ChunkInstruction {
    // [chunk account]
    // [signer]
    // [metadata token]
    InitChunk,

    // [chunk account]
    // [signer]
    // [token]
    UpdateChunk {
        offset: usize,
        data: Box<[u8]>
    }
}

impl ChunkInstruction {
    pub fn unpack (input: & [u8]) -> Result<Self, ProgramError> {
        let (tag, _rest) = input.split_first().ok_or(InvalidInstruction)?;
        let  payload = input.split_at(4).1;

        msg!("Unpacking... Tag: {}, Size: {}", tag, input.len());
        msg!("Payload: {:?}", payload);


        Ok (
            match tag {
                0 => Self::InitChunk,
                1 => Self::UpdateChunk {
                    offset: usize::try_from(Conversations::as_u32_le(payload.split_at(4).0.try_into().unwrap())).unwrap(),
                    data: Box::from(payload.split_at(4).1)
                },
                _ => return Err(InvalidInstruction.into())
            }
        )
    }
}