use std::str::FromStr;
use solana_program::{
    account_info::{next_account_info, AccountInfo},
    entrypoint::ProgramResult,
    msg,
    pubkey::Pubkey,
    program_error::ProgramError,
};

use spl_token::state::{Account as TokenAccount};

use crate::instruction::ChunkInstruction;
use crate::instruction::ChunkInstruction::{InitChunk, UpdateChunk};

use solana_program::program_pack::Pack;
use crate::state::{ChunkAccount, MetadataAccount};

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
            InitChunk  => Self::init_chunk(chunk_account, token),
            UpdateChunk {offset, data} => Self::update_chunk(chunk_account, signer_account, token, &data, offset)
        }
    }

    pub fn init_chunk(chunk_account: &AccountInfo, metadata_token: &AccountInfo) -> ProgramResult {
        let creator = Pubkey::from_str("HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU").unwrap();
        let metadata_owner = Pubkey::from_str("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s").unwrap();

        msg!("Init instruction");
        let mut chunk_data = ChunkAccount::new(&chunk_account.data.borrow())?;
        let metadata = MetadataAccount::new(&metadata_token.data.borrow());

        if !chunk_data.owner_token.to_bytes().iter().all(|&x| x == 0) {
            return Err(ProgramError::AccountAlreadyInitialized);
        }

        if metadata.creator != creator || *metadata_token.owner != metadata_owner {
            return Err(ProgramError::IllegalOwner);
        }

        msg!("Setting id and owner!");
        msg!("ID: {}", metadata.id);
        msg!("Owner token: {}", metadata.mint);

        chunk_data.id = metadata.id;
        chunk_data.owner_token = metadata.mint;
        chunk_data.serialize(chunk_account.try_borrow_mut_data()?);


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

        if data.len() + offset > &chunk_account.data.borrow().len() - ChunkAccount::CHUNK_ACCOUNT_SIZE {
            msg!("Invalid data. Too much numbers");
            return Err(ProgramError::InvalidInstructionData);
        }


        msg!("Data length: {}", data.len());
        ChunkAccount::update(chunk_account.try_borrow_mut_data()?, offset, data);

        Ok(())
    }
}