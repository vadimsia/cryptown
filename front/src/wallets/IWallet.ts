import type {
    Connection,
    PublicKey, SignatureResult, Transaction
} from "@solana/web3.js"

export interface IWallet {
    publicKey: PublicKey
    connection: Connection
    signAndSendTransaction (transaction: Transaction) : Promise<SignatureResult>
}