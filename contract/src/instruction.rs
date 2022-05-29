use std::convert::{TryFrom, TryInto};
use solana_program::program_error::ProgramError;
use solana_program::msg;
use crate::error::ChunkError::InvalidInstruction;
use crate::processor::ChunkAccount;

pub enum ChunkInstruction {
    // [chunk account]
    // [signer]
    // [token]
    InitChunk {
        id: u32
    },

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
                0 => Self::InitChunk {
                    id: ChunkAccount::as_u32_le(payload.try_into().unwrap())
                },
                1 => Self::UpdateChunk {
                    offset: usize::try_from(ChunkAccount::as_u32_le(payload.split_at(4).0.try_into().unwrap())).unwrap(),
                    data: Box::from(payload.split_at(4).1)
                },
                _ => return Err(InvalidInstruction.into())
            }
        )
    }
}