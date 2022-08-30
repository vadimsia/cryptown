use std::cell::RefMut;
use solana_program::program_error::ProgramError;
use solana_program::pubkey::Pubkey;
use std::str;
use crate::utils::Conversations;

pub struct ChunkAccount {
    pub id: u32,
    pub owner_token: Pubkey
}

pub struct MetadataAccount {
    pub id: u32,
    pub creator: Pubkey,
    pub mint: Pubkey
}

impl ChunkAccount {
    pub const CHUNK_ACCOUNT_SIZE: usize = 36;


    pub(crate) fn serialize(&self, mut dst: RefMut<&mut [u8]>) {

        for (i, b) in Conversations::as_u8_be(self.id).iter().enumerate() {
            dst[i] = *b;
        }

        for (i, b) in self.owner_token.to_bytes().iter().enumerate() {
            dst[i + 4] = *b;
        }
    }

    pub(crate) fn update(mut dst: RefMut<&mut [u8]>, offset: usize, data: &[u8]) {
        for (i, b) in data.iter().enumerate() {
            let temp: usize = i + ChunkAccount::CHUNK_ACCOUNT_SIZE + offset;
            dst[temp] = *b;
        }
    }

    pub fn new (data: &[u8]) -> Result<ChunkAccount, ProgramError> {
        if data.len() <= ChunkAccount::CHUNK_ACCOUNT_SIZE {
            return Err(ProgramError::AccountDataTooSmall);
        }

        let account = ChunkAccount {
            id: Conversations::as_u32_be( & data[0..4]),
            owner_token: Pubkey::new(&data[4..36])
        };

        Ok(account)
    }
}

impl MetadataAccount {
    fn parse_id (data: &[u8]) -> Result<u32, ProgramError> {
        let mut string_end: usize = 0;
        for n in 69..data.len() {
            if data[n] == 0 || data[n] == 10 {
                string_end = usize::from(n);
                break;
            }
        }

        let name = str::from_utf8(&data[69..string_end]).unwrap();

        let parts = name.split("#").collect::<Vec<&str>>();

        if parts.len() < 2 {
            return Err(ProgramError::InvalidInstructionData);
        }


        Ok(parts[1].parse::<u32>().unwrap())
    }

    pub fn new (data: &[u8]) -> MetadataAccount {
        let account = MetadataAccount {
            id: MetadataAccount::parse_id(data).unwrap(),
            creator: Pubkey::new(&data[1..33]),
            mint: Pubkey::new(&data[33..65])
        };

        account
    }
}

