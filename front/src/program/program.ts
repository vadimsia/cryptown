import {
    PublicKey,
    Transaction,
    TransactionInstruction
} from "@solana/web3.js";

import type {
    AccountInfo
} from "@solana/web3.js"

import type { IWallet } from "src/wallets/IWallet";

export class Program {
    private _TOKEN_PROGRAM_ID = new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")

    private _programID: PublicKey
    private _wallet: IWallet


    constructor (programID: PublicKey, wallet: IWallet) {
        this._programID = programID
        this._wallet = wallet
    }

    async getAccounts () : Promise<{ pubkey: PublicKey, account: AccountInfo<Buffer> }[]> {
        
        return (await this._wallet.connection.getTokenAccountsByOwner(this._wallet.publicKey, {programId: this._TOKEN_PROGRAM_ID})).value
    }

    async updateChunk (account: PublicKey, token: PublicKey, data: number[]) : Promise<void> {

        let transaction = new Transaction().add(
            new TransactionInstruction({
                keys: [
                    {pubkey: account, isSigner: false, isWritable: true},
                    {pubkey: this._wallet.publicKey, isSigner: true, isWritable: false},
                    {pubkey: token, isSigner: false, isWritable: false}
                ],
                programId: this._programID,
                data: Buffer.from([1, 0, 0, 0].concat(data))
            })
        )
        
        let signature = await this._wallet.signAndSendTransaction(transaction)
        
        console.log(`Update data signature: ${signature}`)
    }
}