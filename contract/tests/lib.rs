use std::str::FromStr;
use solana_program_test::*;

use helloworld::processor::Processor;

use solana_sdk::{
    account::Account,
    instruction::{AccountMeta, Instruction},
    pubkey::Pubkey,
    signature::Signer,
    transaction::Transaction
};
use solana_sdk::signature::Keypair;
use helloworld::state::{ChunkAccount, MetadataAccount};


#[tokio::test]
async fn metadata_test() {
    let data:[u8;100] = [
        4, 240, 163, 60, 253, 14, 237, 57, 28, 90, 65, 128, 246, 97, 156, 67, 46, 83, 180, 186, 47, 23, 110, 97, 86, 125, 47, 58, 133, 244, 174, 210, 205, 12, 211, 209, 117, 109, 244, 127, 246, 60, 145, 109, 196, 165, 52, 72, 86, 149, 142, 3, 79, 244, 102, 134, 190, 27, 228, 1, 203, 148, 74, 109, 146, 32, 0, 0, 0, 56, 66, 105, 116, 32, 83, 111, 108, 97, 110, 97, 32, 80, 97, 110, 100, 97, 32, 35, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ];

    let metadata = MetadataAccount::new(&data);
    println!("{:?}", metadata.creator);
    println!("{:?}", metadata.mint);
    println!("{:?}", metadata.id);

}

#[tokio::test]
async fn test_all() {
    let program_id = Pubkey::new_unique();
    let greeted_pubkey = Pubkey::new_unique();
    let token_account_pubkey = Pubkey::new_unique();
    let metadata_pubkey = Pubkey::new_unique();
    let metadata_owner: Pubkey = Pubkey::from_str("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s").unwrap();

    let owner_account = Keypair::new();

    let mut program_test = ProgramTest::new(
        "helloworld", // Run the BPF version with `cargo test-bpf`
        program_id,
        processor!(Processor::process), // Run the native version with `cargo test`
    );

    program_test.add_account(
        greeted_pubkey,
        Account {
            lamports: 5,
            data: vec![0_u8; 36 + 10000],
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

    let metadata = vec![
        4, 240, 163, 60, 253, 14, 237, 57, 28, 90, 65, 128, 246, 97, 156, 67, 46, 83, 180, 186, 47, 23, 110, 97, 86, 125, 47, 58, 133, 244, 174, 210, 205, 12, 211, 209, 117, 109, 244, 127, 246, 60, 145, 109, 196, 165, 52, 72, 86, 149, 142, 3, 79, 244, 102, 134, 190, 27, 228, 1, 203, 148, 74, 109, 146, 32, 0, 0, 0, 56, 66, 105, 116, 32, 83, 111, 108, 97, 110, 97, 32, 80, 97, 110, 100, 97, 32, 35, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ];

    program_test.add_account(
        metadata_pubkey,
        Account {
            lamports: 100,
            data: metadata,
            owner: metadata_owner,
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
            &[0], // ignored but makes the instruction unique in the slot
            vec![
                AccountMeta::new(greeted_pubkey, false),
                AccountMeta::new(owner_account.pubkey(), true),
                AccountMeta::new(metadata_pubkey, false)
            ],
        )],
        Some(&payer.pubkey()),
    );
    transaction.sign(&[&payer, &owner_account], recent_blockhash);
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
    if let Ok(data) = ChunkAccount::new(&greeted_account.data) {
        println!("{}", data.id);
        println!("{}", data.owner_token.to_string());
    }
}