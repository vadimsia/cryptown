use std::mem;
use solana_program::{
    account_info::{next_account_info, AccountInfo},
    entrypoint::ProgramResult,
    msg,
    pubkey::Pubkey,
    program_error::ProgramError
};

use spl_token::state::{Account as TokenAccount};

use crate::instruction::ChunkInstruction;
use crate::instruction::ChunkInstruction::{InitChunk, UpdateChunk, UpdateToken};

use borsh::{BorshDeserialize, BorshSerialize};
use solana_program::program_pack::Pack;
use std::convert::AsMut;

// #[derive(BorshSerialize, BorshDeserialize, Debug)]
// pub struct ChunkAccount {
//     pub id: i32,
//     pub daddy: Pubkey,
//     pub owner_token: Pubkey
// }


pub struct ChunkAccount {
    pub id: u32,
    pub daddy: Pubkey,
    pub owner_token: Pubkey,
    pub data: Box<[u8]>
}

impl ChunkAccount {
    fn as_u32_be(array: &[u8]) -> u32 {
        ((array[0] as u32) << 24) +
            ((array[1] as u32) << 16) +
            ((array[2] as u32) <<  8) +
            ((array[3] as u32) <<  0)
    }

    pub fn new (data: &[u8]) -> Result<ChunkAccount, ProgramError> {

        msg!("data len: {:?}", data);
        if data.len() <= 68 {
            msg!("too small");
            return Err(ProgramError::AccountDataTooSmall);
        }
        msg!("craeting account");

        let account = ChunkAccount {
            id: ChunkAccount::as_u32_be( & data[0..4]),
            daddy: Pubkey::new(&data[4..36]),
            owner_token: Pubkey::new(&data[36..68]),
            data: Box::from(&data[68..data.len()])
        };

        msg!("id: {}", account.id);

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
            InitChunk => Self::init_chunk(chunk_account, signer_account),
            UpdateChunk {data} => Self::update_chunk(chunk_account, signer_account,next_account_info(accounts_iter).unwrap(), &data),
            UpdateToken  => Self::update_token(chunk_account,  signer_account, next_account_info(accounts_iter).unwrap())
        }
    }

    pub fn init_chunk(chunk_account: &AccountInfo, signer_account: &AccountInfo) -> ProgramResult {
        msg!("Init instruction");
        let mut chunk_data = ChunkAccount::new(&chunk_account.data.borrow())?;

        let pb: Pubkey = chunk_data.daddy;

        msg!("Daddy is: {:?}", pb);

        if pb.to_bytes().iter().all(|&x| x == 0) {
            msg!("Setting daddy!");
            chunk_data.daddy = *signer_account.key;
            // chunk_account.data.borrow_mut() = chunk_data
            // chunk_data.serialize(&mut &mut chunk_account.data.borrow_mut()[..])?;
        }

        Ok(())
    }

    pub fn update_chunk(chunk_account: &AccountInfo, signer_account: &AccountInfo, token: &AccountInfo, data: &[u8]) -> ProgramResult {
        let mut chunk_data: ChunkAccount = ChunkAccount::new(&chunk_account.data.borrow())?;
        msg!("Unpacking spl account data {:?}", token.data);
        // let spl_token_account = TokenAccount::unpack(&token.data.borrow())?;
        //
        // msg!("Matching owner");
        // msg!("Mint: {}", spl_token_account.mint.to_string());
        // msg!("Amount: {}", spl_token_account.amount);


        // if chunk_data.owner_token != spl_token_account.mint {
        //     return Err(ProgramError::InvalidInstructionData);
        // }
        //
        // if spl_token_account.owner != *signer_account.key || spl_token_account.amount == 0 {
        //     return Err(ProgramError::IllegalOwner);
        // }

        if data.len() != &chunk_account.data.borrow().len() - mem::size_of::<ChunkAccount>() {
            msg!("Invalid data. Excepted length: {}, got: {}",  chunk_account.data.borrow().len() - mem::size_of::<ChunkAccount>(), data.len());
            return Err(ProgramError::InvalidInstructionData);
        }


        msg!("Data length: {}", data.len());
        //chunk_data.data = data;
        //chunk_data.serialize(&mut &mut chunk_account.data.borrow_mut()[..])?;

        Ok(())
    }

    pub fn update_token(chunk_account: &AccountInfo, signer_account: &AccountInfo, token_account: &AccountInfo) -> ProgramResult {
        // let mut chunk_data: ChunkAccount = ChunkAccount::try_from_slice(&chunk_account.data.borrow())?;
        //
        // if chunk_data.daddy != *signer_account.key {
        //     msg!("Invalid daddy!");
        //     return Err(ProgramError::IllegalOwner);
        // }
        //
        // if !chunk_data.owner_token.to_bytes().iter().all(|&x| x == 0) {
        //     return Err(ProgramError::AccountAlreadyInitialized);
        // }
        //
        // msg!("Setting token: {}", token_account.key.to_string());
        //
        // chunk_data.owner_token = *token_account.key;
        // chunk_data.serialize(&mut &mut chunk_account.data.borrow_mut()[..])?;
        Ok(())
    }
}