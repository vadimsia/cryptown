import type { Connection, PublicKey, Transaction } from '@solana/web3.js';

export type Wallet = {
	publicKey: PublicKey;
	connection: Connection;

	sendTransaction(transaction: Transaction): Promise<string>;
	signTransaction(transaction: Transaction) : Promise<Transaction>;
};
