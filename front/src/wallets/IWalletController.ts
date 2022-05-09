import type { Connection } from "@solana/web3.js"
import type { IWallet } from "./IWallet"

export interface IWalletController {
    Wallet: IWallet
    Connection: Connection

    connect () : Promise<void>
}