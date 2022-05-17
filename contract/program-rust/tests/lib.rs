use std::borrow::Borrow;
use borsh::BorshDeserialize;
use solana_program_test::*;

use helloworld::processor::Processor;
use helloworld::processor::{ChunkAccount};

use solana_sdk::{
    account::Account,
    instruction::{AccountMeta, Instruction},
    pubkey::Pubkey,
    signature::Signer,
    transaction::Transaction,
};

use spl_token::{id, instruction, state::{Account as TokenAccount, Mint}};


use std::mem;
use std::str::FromStr;
use solana_program::program_pack::Pack;
use solana_sdk::account::ReadableAccount;
use solana_sdk::signature::Keypair;

#[tokio::test]
async fn initialize_mint () {
    let program_test = ProgramTest::default();
    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    // CREATING MINT


    let mint_account = Keypair::new();
    let owner = Keypair::new();
    let token_program = &id();

    let rent = banks_client.get_rent().await.unwrap();
    let mint_rent = rent.minimum_balance(Mint::LEN);

    let token_mint_account_ix = solana_program::system_instruction::create_account(
        &payer.pubkey(),
        &mint_account.pubkey(),
        mint_rent,
        Mint::LEN as u64,
        token_program
    );

    let token_mint_a_ix = instruction::initialize_mint(
        token_program,
        &mint_account.pubkey(),
        owner.pubkey().borrow(),
        None,
        9
    ).unwrap();

    let token_mint_a_tx = Transaction::new_signed_with_payer(
        &[token_mint_account_ix, token_mint_a_ix],
        Some(&payer.pubkey()),
        &[&payer, &mint_account],
        recent_blockhash
    );

    banks_client.process_transaction(token_mint_a_tx).await.unwrap();

    let token_account = Keypair::new();
    let rent = banks_client.get_rent().await.unwrap();
    let account_rent = rent.minimum_balance(TokenAccount::LEN);

    // CREATING TOKEN ACCOUNT


    let token_account_ix = solana_program::system_instruction::create_account(
        &payer.pubkey(),
        &token_account.pubkey(),
        account_rent,
        TokenAccount::LEN as u64,
        token_program
    );

    let my_account = Keypair::new();

    let my_account_ix = solana_program::system_instruction::create_account(
        &payer.pubkey(),
        &my_account.pubkey(),
        account_rent,
        5,
        token_program
    );

    let initialize_account_ix= instruction::initialize_account(
        token_program,
        &token_account.pubkey(),
        &mint_account.pubkey(),
        &my_account.pubkey()
    ).unwrap();

    let create_token_account_tx = Transaction::new_signed_with_payer(
        &[token_account_ix, initialize_account_ix, my_account_ix],
        Some(&payer.pubkey()),
        &[&payer, &token_account, &my_account],
        recent_blockhash
    );

    banks_client.process_transaction(create_token_account_tx).await.unwrap();


    // MINTING TOKENS
    let amount: u64 = 10;
    let mint_to_ix = instruction::mint_to(
        &token_program,
        &mint_account.pubkey(),
        &token_account.pubkey(),
        &owner.pubkey(),
        &[],
        amount
    ).unwrap();

    let mint_to_tx = Transaction::new_signed_with_payer(
        &[mint_to_ix],
        Some(&payer.pubkey()),
        &[&payer, &owner],
        recent_blockhash
    );

    banks_client.process_transaction(mint_to_tx).await.unwrap();


    // Show acc data

    let account_info = banks_client
        .get_account(token_account.pubkey()).await.unwrap().expect("cant fetch acc info");

    let account_data = TokenAccount::unpack(account_info.data()).unwrap();
    println!("Account data: {:?}", account_data);
    println!("My account: {}", my_account.pubkey().to_string());

    let my_account_info = banks_client.get_account(my_account.pubkey()).await.unwrap().expect("Cant fetch my acc info");
    println!("My Account data: {:?}", my_account_info);
}

#[tokio::test]
async fn test_init() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; mem::size_of::<ChunkAccount>()],
            owner: program_id,
            ..Account::default()
        },
    );
    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    // Greet once
    for i in 0..2 {
        let mut transaction = Transaction::new_with_payer(
            &[Instruction::new_with_bincode(
                program_id,
                &[0, i], // ignored but makes the instruction unique in the slot
                vec![
                    AccountMeta::new(greeted_pubkey, false),
                    AccountMeta::new(payer.pubkey(), true)
                ],
            )],
            Some(&payer.pubkey()),
        );
        transaction.sign(&[&payer], recent_blockhash);
        banks_client.process_transaction(transaction).await.unwrap();
    }

    // Verify account has one greeting
    let greeted_account = banks_client
        .get_account(greeted_pubkey)
        .await
        .expect("get_account")
        .expect("greeted_account not found");

    let chunk_data = ChunkAccount::try_from_slice(&greeted_account.data).unwrap();

    assert_eq!(chunk_data.daddy, payer.pubkey());
}

#[tokio::test]
async fn test_update_data() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();
    let token_pubkey = Pubkey::new_unique();

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; mem::size_of::<ChunkAccount>()],
            owner: program_id,
            ..Account::default()
        },
    );

    program_test.add_account(
        token_pubkey,
        Account {
            lamports: 50,
            data: vec![1, 0, 0, 0, 70, 127, 112, 132, 70, 252, 26, 28, 96, 54, 144, 252, 19, 188, 85, 142, 247, 91, 205, 117, 126, 246, 50, 168, 17, 179, 47, 16, 55, 35, 236, 143, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 70, 127, 112, 132, 70, 252, 26, 28, 96, 54, 144, 252, 19, 188, 85, 142, 247, 91, 205, 117, 126, 246, 50, 168, 17, 179, 47, 16, 55, 35, 236, 143],
            owner: program_id,
            ..Account::default()
        }
    );

    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[0], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true)
            ],
        )],
        Some(&payer.pubkey()),
    );
    transaction.sign(&[&payer], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();


    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[1, 1, 1], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true),
                AccountMeta::new(token_pubkey, false)
            ],
        )],
        Some(&payer.pubkey()),
    );
    transaction.sign(&[&payer], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();

    // Verify account has one greeting
    let greeted_account = banks_client
        .get_account(greeted_pubkey)
        .await
        .expect("get_account")
        .expect("greeted_account not found");

    let chunk_data = ChunkAccount::try_from_slice(&greeted_account.data).unwrap();

    assert_eq!(chunk_data.data[0], 1);
    assert_eq!(chunk_data.data[4], 1);
    assert_eq!(chunk_data.data[5], 0);
    assert_eq!(chunk_data.daddy, payer.pubkey());
}

#[tokio::test]
async fn test_update_token() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();
    let owner_token = Pubkey::from_str("G5kGWLs2uTY5ygosKnnW9d6UiZwqSPKdzJExnwBE2vXT").unwrap();

    println!("token: {}", owner_token.to_string());

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; mem::size_of::<ChunkAccount>()],
            owner: program_id,
            ..Account::default()
        },
    );
    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[0], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true)
            ],
        )],
        Some(&payer.pubkey()),
    );

    transaction.sign(&[&payer], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();


    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[2], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true),
                AccountMeta::new(owner_token, false)
            ],
        )],
        Some(&payer.pubkey()),
    );

    transaction.sign(&[&payer], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();

    // Verify account has one greeting
    let greeted_account = banks_client
        .get_account(greeted_pubkey)
        .await
        .expect("get_account")
        .expect("greeted_account not found");

    let chunk_data = ChunkAccount::try_from_slice(&greeted_account.data).unwrap();

    assert_eq!(chunk_data.owner_token, owner_token);
    assert_eq!(chunk_data.daddy, payer.pubkey());
}