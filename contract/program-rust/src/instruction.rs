use std::convert::TryInto;
use solana_program::program_error::ProgramError;
use solana_program::msg;
use crate::error::ChunkError::InvalidInstruction;
use crate::processor::ChunkAccount;

pub enum ChunkInstruction {
    // [chunk account]
    // [signer]
    InitChunk {
        id: u32
    },

    // [chunk account]
    // [signer]
    // [token]
    UpdateChunk {
        offset: u32,
        data: Box<[u8]>
    },

    // [chunk account]
    // [signer]
    // [token]
    UpdateToken,
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
                    offset: ChunkAccount::as_u32_le(payload.split_at(4).0.try_into().unwrap()),
                    data: Box::from(payload.split_at(4).1)
                },
                2 => Self::UpdateToken,
                _ => return Err(InvalidInstruction.into())
            }
        )
    }
}