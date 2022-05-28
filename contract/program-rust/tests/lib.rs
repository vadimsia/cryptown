use std::str::FromStr;
use solana_program_test::*;

use helloworld::processor::Processor;
use helloworld::processor::{ChunkAccount};

use solana_sdk::{
    account::Account,
    instruction::{AccountMeta, Instruction},
    pubkey::Pubkey,
    signature::Signer,
    transaction::Transaction
};
use solana_sdk::signature::Keypair;


#[tokio::test]
async fn test_init() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();
    let token = Pubkey::new_unique();
    let wrong_token = Pubkey::new_unique();

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; 36 + 10],
            owner: program_id,
            ..Account::default()
        },
    );
    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    // First init
    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[0, 1500], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true),
                AccountMeta::new(token, false)
            ],
        )],
        Some(&payer.pubkey()),
    );
    transaction.sign(&[&payer], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();

    // Second init
    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[0, 1501], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(payer.pubkey(), true),
                AccountMeta::new(wrong_token, false)
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

    let chunk_data = ChunkAccount::new(&greeted_account.data);

    if let Ok(data) = chunk_data {
        assert_eq!(data.owner_token, token);
        assert_eq!(data.id, 1500);
        println!("{:?}", greeted_account.data);
        println!("{:?}", token.to_bytes().len());
    }
}

#[tokio::test]
async fn test_update_data() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();
    let token_account_pubkey = Pubkey::new_unique();
    let token_pubkey;

    let owner_account = Keypair::new();

    if let Ok(pk) = Pubkey::from_str("s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV") {
        token_pubkey = pk
    } else { panic!("Wrong pk") }

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; 68 + 32768],
            owner: program_id,
            ..Account::default()
        },
    );

    let pk = owner_account.pubkey().to_bytes();

    let token_account_data = vec![12, 211, 209, 117, 109, 244, 127, 246,  60, 145, 109, 196,
                                  165,  52,  72,  86, 149, 142,   3,  79, 244, 102, 134, 190,
                                  27, 228,   1, 203, 148,  74, 109, 146, pk[0], pk[1],  pk[2], pk[3],
                                  pk[4], pk[5],  pk[6],  pk[7],  pk[8],  pk[9], pk[10], pk[11],  pk[12], pk[13],  pk[14],  pk[15],
                                  pk[16], pk[17], pk[18],  pk[19],  pk[20], pk[21],  pk[22],  pk[23], pk[24],  pk[25],  pk[26], pk[27],
                                  pk[28], pk[29], pk[30], pk[31],   1,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0];

    program_test.add_account(
        token_account_pubkey,
        Account {
            lamports: 50,
            data: token_account_data,
            owner: program_id,
            ..Account::default()
        }
    );

    let (mut banks_client, payer, recent_blockhash) = program_test.start().await;

    let owner_account_ix = solana_program::system_instruction::create_account(
        &payer.pubkey(),
        &owner_account.pubkey(),
        1_000_000_000,
        10,
        &program_id
    );

    let owner_account_tx = Transaction::new_signed_with_payer(
        &[owner_account_ix],
        Some(&payer.pubkey()),
        &[&payer, &owner_account],
        recent_blockhash
    );

    banks_client.process_transaction(owner_account_tx).await.unwrap();

    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[0, 0], // ignored but makes the instruction unique in the slot
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


    let mut transaction = Transaction::new_with_payer(
        &[Instruction::new_with_bincode(
            program_id,
            &[1, 0, 1, 2, 3, 4], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(owner_account.pubkey(), true),
                AccountMeta::new(token_account_pubkey, false)
            ],
        )],
        Some(&payer.pubkey()),
    );
    transaction.sign(&[&payer, &owner_account], recent_blockhash);
    banks_client.process_transaction(transaction).await.unwrap();


    // Verify account has one greeting
    let greeted_account = banks_client
        .get_account(greeted_pubkey)
        .await
        .expect("get_account")
        .expect("greeted_account not found");


    println!("{:?}", greeted_account.data.split_at(36).1);
    //
    // assert_eq!(chunk_data.data[0], 1);
    // assert_eq!(chunk_data.data[4], 1);
    // assert_eq!(chunk_data.data[5], 0);
    // assert_eq!(chunk_data.daddy, payer.pubkey());
}