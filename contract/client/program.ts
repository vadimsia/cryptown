import { 
    AccountInfo,
    Connection,
    ParsedAccountData,
    PublicKey,
    Keypair,
    Transaction,
    SystemProgram,
    sendAndConfirmTransaction, 
    TransactionInstruction
} from "@solana/web3.js";

export class Program {
    private programID: PublicKey
    private payer: Keypair
    private connection: Connection

    private readonly CHUNKS_NUM = 5
    private readonly ACCOUNT_SPACE = 32768 + 68

    constructor (programID: PublicKey, payer: Keypair, connection: Connection) {
        this.programID = programID
        this.payer = payer
        this.connection = connection
    }

    async getOrCreateAccounts () : Promise<{pubkey: PublicKey, account: AccountInfo<Buffer | ParsedAccountData>}[]> {
        let result = await this.connection.getParsedProgramAccounts(this.programID)
        
        if (result.length < this.CHUNKS_NUM) {
            let signers = [this.payer]
            let transaction = new Transaction()

            console.log('Creating accounts...')

            for (let i = 0; i < this.CHUNKS_NUM - result.length; i++) {
                let account = Keypair.generate()
                
                let lamports = await this.connection.getMinimumBalanceForRentExemption(this.ACCOUNT_SPACE)

                transaction.add(
                    SystemProgram.createAccount({
                        fromPubkey: this.payer.publicKey,
                        lamports,
                        newAccountPubkey: account.publicKey,
                        programId: this.programID,
                        space: this.ACCOUNT_SPACE
                    })
                )

                signers.push(account)

                if (signers.length == 6) {
                    let signature = await sendAndConfirmTransaction(this.connection, transaction, signers)
                    console.log(`Create account signature: ${signature}`)

                    transaction = new Transaction()
                    signers = [this.payer]
                }
            }
            
            if (signers.length > 1) {
                let signature = await sendAndConfirmTransaction(this.connection, transaction, signers)
                console.log(`Create account signature: ${signature}`)
            }
        }

        result = await this.connection.getParsedProgramAccounts(this.programID)
        
        return result
    }
    
    async initAccounts (accounts: PublicKey[]) : Promise<void> {
        for (let i in accounts) {
            let account = accounts[i]

            let data = Buffer.from([0, 0, 0, 0, 0, 0, 0, 0])
            data.writeInt32LE(Number(i), 4)

            let transaction = new Transaction().add(
                new TransactionInstruction({
                    keys: [
                        {pubkey: account, isSigner: false, isWritable: true},
                        {pubkey: this.payer.publicKey, isSigner: true, isWritable: false}
                    ],
                    programId: this.programID,
                    data: data
                })
            )

            

            let signature = await sendAndConfirmTransaction(this.connection, transaction, [this.payer])
            console.log(`Init account signature: ${signature}`)
        }
    }

    async updateChunk (account: PublicKey, token: PublicKey, offset: number, data: number[]) : Promise<void> {
        let offset_buf = Buffer.alloc(4)
        offset_buf.writeUInt32LE(offset, 0)

        let transaction = new Transaction().add(
            new TransactionInstruction({
                keys: [
                    {pubkey: account, isSigner: false, isWritable: true},
                    {pubkey: this.payer.publicKey, isSigner: true, isWritable: false},
                    {pubkey: token, isSigner: false, isWritable: false}
                ],
                programId: this.programID,
                data: Buffer.from([1, 0, 0, 0].concat(...offset_buf).concat(data))
            })
        )

        let signature = await sendAndConfirmTransaction(this.connection, transaction, [this.payer])
        
        console.log(`Update data signature: ${signature}`)
    }

    async updateTokens (accounts: PublicKey[], tokens: PublicKey[]) : Promise<void> {
        for (let i = 0; i < accounts.length; i++) {
            let account = accounts[i]
            let token = tokens[i]

            let transaction = new Transaction().add(
                new TransactionInstruction({
                    keys: [
                        {pubkey: account, isSigner: false, isWritable: true},
                        {pubkey: this.payer.publicKey, isSigner: true, isWritable: false},
                        {pubkey: token, isSigner: false, isWritable: false}
                    ],
                    programId: this.programID,
                    data: Buffer.from([2, 0, 0, 0])
                })
            )

            let signature = await sendAndConfirmTransaction(this.connection, transaction, [this.payer])
            console.log(`Update token signature: ${signature}`)
        }
    }
}