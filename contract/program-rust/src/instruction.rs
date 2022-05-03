use std::convert::{TryInto};
use solana_program::program_error::ProgramError;
use solana_program::msg;
use solana_program::pubkey::Pubkey;
use crate::error::ChunkError::InvalidInstruction;

pub enum ChunkInstruction {
    // accounts in every instruction:
    // [chunk account]
    // [signer]

    InitChunk,
    UpdateChunk {
        data: [u8; 8]
    },
    UpdateToken {
        token: Pubkey
    },
}

impl ChunkInstruction {
    pub fn unpack (input: & [u8]) -> Result<Self, ProgramError> {
        let (tag, _rest) = input.split_first().ok_or(InvalidInstruction)?;
        let payload = input.split_at(4).1;

        msg!("Unpacking... Tag: {}, Size: {}", tag, input.len());


        Ok (
            match tag {
                0 => Self::InitChunk,
                1 => Self::UpdateChunk {
                    data: payload.try_into().unwrap()
                },
                2 => Self::UpdateToken {
                    token: Pubkey::new_from_array(payload.try_into().unwrap())
                },
                _ => return Err(InvalidInstruction.into())
            }
        )
    }
}