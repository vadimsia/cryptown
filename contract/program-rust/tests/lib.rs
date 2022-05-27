use std::convert::TryInto;
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

    let token_account_data = vec![12, 211, 209, 117, 109, 244, 127, 246,  60, 145, 109, 196,
                                  165,  52,  72,  86, 149, 142,   3,  79, 244, 102, 134, 190,
                                  27, 228,   1, 203, 148,  74, 109, 146, 240, 163,  60, 253,
                                  14, 237,  57,  28,  90,  65, 128, 246,  97, 156,  67,  46,
                                  83, 180, 186,  47,  23, 110,  97,  86, 125,  47,  58, 133,
                                  244, 174, 210, 205,   1,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                                  0,   0,   0,   0,   0,   0,   0,   0,   0];

    println!("{:?}", Pubkey::new_from_array(token_account_data.split_at(32).1.split_at(32).0.try_into().unwrap()));

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



    println!("{:?}", payer.pubkey().to_string());

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
                AccountMeta::new(payer.pubkey(), true),
                AccountMeta::new(token_account_pubkey, false)
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


    println!("{:?}", greeted_account.data);
    //
    // assert_eq!(chunk_data.data[0], 1);
    // assert_eq!(chunk_data.data[4], 1);
    // assert_eq!(chunk_data.data[5], 0);
    // assert_eq!(chunk_data.daddy, payer.pubkey());
}