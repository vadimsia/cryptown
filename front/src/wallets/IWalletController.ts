import type { Connection } from '@solana/web3.js';
import type { Wallet } from './IWallet';

export interface IWalletController {
	wallet: Wallet;
	connection: Connection;

	connect(): Promise<void>;
}
