import { Connection, Keypair, PublicKey, type Transaction } from '@solana/web3.js';
import type { Wallet } from './IWallet';
import type { IWalletController } from './IWalletController';

export interface SignedMessage {
	publicKey: PublicKey;
	signature: Uint8Array[];
}

interface PhantomProvider {
	connect(): Promise<{ publicKey: PublicKey }>;
	signTransaction(transaction: Transaction): Promise<Transaction>;
	signAllTransactions(transactions: Transaction[]): Promise<Transaction[]>;
	signMessage(encoded: Uint8Array, charset: string): Promise<SignedMessage>;
}

declare global {
	interface Window {
		solana?: PhantomProvider;
	}
}

export class PhantomWallet implements IWalletController {
	private _wallet: Wallet;
	private _solana_interface?: PhantomProvider;
	private _connection: Connection;

	constructor() {
		if (globalThis.window != undefined)
			this._solana_interface = window.solana;

		this._connection = new Connection(
			import.meta.env.VITE_SOLANA_RPC || 'https://api.devnet.solana.com'
		);
		//this._connection = new Connection('https://api.mainnet-beta.solana.com');

		this._wallet = {
			publicKey: Keypair.generate().publicKey,
			connection: this._connection,
			loggedIn: false,
			sendTransaction: this.sendTransaction.bind(this),
			sendRawTransaction: this.sendRawTransaction.bind(this),
			sendTransactions: this.sendTransactions.bind(this),
			signTransaction: this.signTransaction.bind(this),
			signAllTransactions: this.signAllTransactions.bind(this),
			signMessage: this.signMessage.bind(this)
		};
	}

	private async signMessage(message: string): Promise<SignedMessage> {
		if (this._solana_interface) {
			const encoded = new TextEncoder().encode(message);
			return await this._solana_interface.signMessage(encoded, 'utf8');
		} else throw 'Cant find phantom wallet';
	}

	private async signTransaction(transaction: Transaction): Promise<Transaction> {
		if (this._solana_interface) {
			return await this._solana_interface.signTransaction(transaction);
		} else throw 'Cant find phantom wallet';
	}

	private async signAllTransactions(transactions: Transaction[]): Promise<Transaction[]> {
		if (this._solana_interface) {
			return await this._solana_interface.signAllTransactions(transactions);
		} else throw 'Cant find phantom wallet';
	}

	private async sendTransaction(transaction: Transaction): Promise<string> {
		if (this._solana_interface) {
			const signed = await this._solana_interface.signTransaction(transaction);
			const signature = await this._connection.sendRawTransaction(signed.serialize());
			await this._connection.confirmTransaction(signature);
			return signature;
		} else throw 'Cant find phantom wallet';
	}

	private async sendRawTransaction(transaction: Transaction): Promise<string> {
		if (this._solana_interface) {
			const signature = await this._connection.sendRawTransaction(transaction.serialize());
			await this._connection.confirmTransaction(signature);
			return signature;
		} else throw 'Cant find phantom wallet';
	}

	private async sendTransactions(transactions: Transaction[]): Promise<string[]> {
		if (this._solana_interface) {
			let signatures: string[] = [];

			const signed = await this._solana_interface.signAllTransactions(transactions);
			for (const transaction of signed) {
				let signature: string;

				try {
					signature = await this._connection.sendRawTransaction(transaction.serialize());
					await this._connection.confirmTransaction(signature);
				} catch (e) {
					console.log('Send transaction error!! ' + e);
					continue;
				}

				signatures.push(signature);
			}

			return signatures;
		} else throw 'Cant find phantom wallet';
	}

	async connect(): Promise<void> {
		if (this._solana_interface) {
			const result = await this._solana_interface.connect();

			this._wallet.publicKey = result.publicKey;
			this._wallet.loggedIn = true;
		} else throw 'Cant connect phantom wallet';
	}

	public get wallet(): Wallet {
		return this._wallet;
	}

	public get connection(): Connection {
		return this._connection;
	}
}
