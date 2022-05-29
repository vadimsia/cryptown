import type { Connection, PublicKey, Transaction } from '@solana/web3.js';
import type { SignedMessage } from './PhantomWallet';

export type Wallet = {
	publicKey: PublicKey;
	connection: Connection;

	sendTransaction(transaction: Transaction): Promise<string>;
	signTransaction(transaction: Transaction) : Promise<Transaction>;
	signMessage(message: string) : Promise<SignedMessage>
};
