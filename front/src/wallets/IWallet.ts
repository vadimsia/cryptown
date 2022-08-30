import type { Connection, PublicKey, Transaction } from '@solana/web3.js';
import type { SignedMessage } from './PhantomWallet';

export type Wallet = {
	publicKey: PublicKey;
	connection: Connection;
	loggedIn: boolean;

	sendTransaction(transaction: Transaction): Promise<string>;
	sendRawTransaction(transaction: Transaction): Promise<string>;
	sendTransactions(transactions: Transaction[]): Promise<string[]>;
	signTransaction(transaction: Transaction): Promise<Transaction>;
	signAllTransactions(transactions: Transaction[]): Promise<Transaction[]>;
	signMessage(message: string): Promise<SignedMessage>;
};
