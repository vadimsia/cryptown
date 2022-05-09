import { Connection, PublicKey, type SignatureResult, type Transaction } from "@solana/web3.js";
import type { IWallet } from "./IWallet";
import type { IWalletController } from "./IWalletController";

interface ConnectResult {
    publicKey: PublicKey
}

interface PhantomProvider {
    connect () : Promise<ConnectResult>
    signAndSendTransaction (transaction: Transaction) : Promise<SignatureResult>
}

export class PhantomWallet implements IWalletController {
    private _wallet: IWallet;
    private _solana_interface: PhantomProvider
    private _connection: Connection

    constructor () {
        // @ts-ignore
        this._solana_interface = window.solana as PhantomProvider
        // @ts-ignore
        this._wallet = null

        this._connection = new Connection("https://api.devnet.solana.com")
    }

    async connect(): Promise<void> {
        let result = await this._solana_interface.connect()
        this._wallet = {
            publicKey: result.publicKey,
            connection: this._connection,
            signAndSendTransaction: this._solana_interface.signAndSendTransaction
        }
    }
    
    public get Wallet () : IWallet {
        return this._wallet
    }

    public get Connection() : Connection {
        return this._connection
    }
}