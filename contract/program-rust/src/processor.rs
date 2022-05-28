use std::cell::RefMut;
use solana_program::{
    account_info::{next_account_info, AccountInfo},
    entrypoint::ProgramResult,
    msg,
    pubkey::Pubkey,
    program_error::ProgramError
};

use spl_token::state::{Account as TokenAccount};

use crate::instruction::ChunkInstruction;
use crate::instruction::ChunkInstruction::{InitChunk, UpdateChunk};

use solana_program::program_pack::Pack;



pub struct ChunkAccount {
    pub id: u32,
    pub owner_token: Pubkey
}

const CHUNK_ACCOUNT_SIZE: usize = 36;

impl ChunkAccount {
    pub fn as_u32_be(array: &[u8]) -> u32 {
        ((array[0] as u32) << 24) +
            ((array[1] as u32) << 16) +
            ((array[2] as u32) <<  8) +
            ((array[3] as u32) <<  0)
    }

    pub fn as_u32_le(array: &[u8]) -> u32 {
        ((array[3] as u32) << 24) +
            ((array[2] as u32) << 16) +
            ((array[1] as u32) <<  8) +
            ((array[0] as u32) <<  0)
    }

    fn as_u8_be(x: u32) -> [u8;4] {
        let b1 : u8 = ((x >> 24) & 0xff) as u8;
        let b2 : u8 = ((x >> 16) & 0xff) as u8;
        let b3 : u8 = ((x >> 8) & 0xff) as u8;
        let b4 : u8 = (x & 0xff) as u8;
        return [b1, b2, b3, b4]
    }

    fn serialize(&self, mut dst: RefMut<&mut [u8]>) {

        for (i, b) in ChunkAccount::as_u8_be(self.id).iter().enumerate() {
            dst[i] = *b;
        }

        for (i, b) in self.owner_token.to_bytes().iter().enumerate() {
            dst[i + 4] = *b;
        }
    }

    fn update(mut dst: RefMut<&mut [u8]>, offset: usize, data: &[u8]) {
        for (i, b) in data.iter().enumerate() {
            let temp: usize = i + CHUNK_ACCOUNT_SIZE + offset;
            dst[temp] = *b;
        }
    }

    pub fn new (data: &[u8]) -> Result<ChunkAccount, ProgramError> {
        if data.len() <= CHUNK_ACCOUNT_SIZE {
            return Err(ProgramError::AccountDataTooSmall);
        }

        let account = ChunkAccount {
            id: ChunkAccount::as_u32_be( & data[0..4]),
            owner_token: Pubkey::new(&data[4..36])
        };

        Ok(account)
    }
}

#[derive(Debug)]
pub struct Processor;


impl Processor {
    pub fn process (
        program_id: &Pubkey,
        accounts: &[AccountInfo],
        instruction_data: &[u8]
    ) -> ProgramResult {
        msg!("Start!");
        let accounts_iter = &mut accounts.iter();

        let chunk_account = next_account_info(accounts_iter)?;
        let signer_account = next_account_info(accounts_iter)?;
        let token = next_account_info(accounts_iter)?;



        if !signer_account.is_signer {
            msg!("Given signer account is not an signer!!");
            return Err(ProgramError::IllegalOwner);
        }

        if chunk_account.owner != program_id {
            msg!("Greeted account does not have the correct program id");
            return Err(ProgramError::IncorrectProgramId);
        }

        msg!("Getting Instruction");
        let instruction = ChunkInstruction::unpack(instruction_data)?;

        msg!("Matching instruiction!");

        match instruction {
            InitChunk {id} => Self::init_chunk(chunk_account, token, id),
            UpdateChunk {offset, data} => Self::update_chunk(chunk_account, signer_account, token, &data, offset)
        }
    }

    pub fn init_chunk(chunk_account: &AccountInfo, mint_account: &AccountInfo, id: u32) -> ProgramResult {
        msg!("Init instruction");
        let mut chunk_data = ChunkAccount::new(&chunk_account.data.borrow())?;

        let pb: Pubkey = chunk_data.owner_token;

        msg!("Owner token is: {:?}", pb);

        if pb.to_bytes().iter().all(|&x| x == 0) {
            msg!("Setting id and owner!");
            chunk_data.id = id;
            chunk_data.owner_token = *mint_account.key;
            chunk_data.serialize(chunk_account.try_borrow_mut_data()?);
        }

        Ok(())
    }

    pub fn update_chunk(chunk_account: &AccountInfo, signer_account: &AccountInfo, token: &AccountInfo, data: &[u8], offset: usize) -> ProgramResult {
        let chunk_data: ChunkAccount = ChunkAccount::new(&chunk_account.data.borrow())?;
        msg!("Unpacking spl account data");

        let spl_token_account = TokenAccount::unpack(&token.data.borrow())?;

        msg!("Matching owner");
        msg!("Mint: {}", spl_token_account.mint.to_string());
        msg!("Amount: {}", spl_token_account.amount);
        msg!("Owner: {}", spl_token_account.owner);


        if chunk_data.owner_token != spl_token_account.mint {
            return Err(ProgramError::InvalidInstructionData);
        }

        if spl_token_account.owner != *signer_account.key || spl_token_account.amount == 0 {
            return Err(ProgramError::IllegalOwner);
        }

        if data.len() + offset > &chunk_account.data.borrow().len() - CHUNK_ACCOUNT_SIZE {
            msg!("Invalid data. Too much numbers");
            return Err(ProgramError::InvalidInstructionData);
        }


        msg!("Data length: {}", data.len());
        ChunkAccount::update(chunk_account.try_borrow_mut_data()?, offset, data);

        Ok(())
    }
}