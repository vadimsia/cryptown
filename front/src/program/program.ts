import { PublicKey, Transaction, TransactionInstruction } from '@solana/web3.js';

import { Buffer } from 'buffer';
import type { Wallet } from '../wallets/IWallet';
import type { ProgramAccount } from './ProgramAccount';
import type { TokenAccount } from './TokenAccount';

export class Program {
	private _TOKEN_PROGRAM_ID = new PublicKey('TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA');

	private _programID: PublicKey;
	private _wallet: Wallet;

	constructor(programID: PublicKey, wallet: Wallet) {
		this._programID = programID;
		this._wallet = wallet;
	}

	/**
	 * @returns all regions of program
	 */
	async getProgramAccounts(): Promise<ProgramAccount[]> {
		const result: ProgramAccount[] = [];
		const accounts = await this._wallet.connection.getProgramAccounts(this._programID);

		for (const account of accounts) {
			result.push({
				publicKey: account.pubkey,
				daddy: new PublicKey(account.account.data.slice(0, 32)),
				owner_token: new PublicKey(account.account.data.slice(32, 64)),
				data: account.account.data.slice(64)
			});
		}

		return result;
	}

	/**
	 * @returns all user tokens (+ nfts)
	 */
	async getUserTokens(): Promise<TokenAccount[]> {
		const result: TokenAccount[] = [];
		const accounts = (
			await this._wallet.connection.getTokenAccountsByOwner(this._wallet.publicKey, {
				programId: this._TOKEN_PROGRAM_ID
			})
		).value;

		for (const account of accounts) {
			result.push({
				publicKey: account.pubkey,
				mint: new PublicKey(account.account.data.slice(0, 32)),
				owner: new PublicKey(account.account.data.slice(32, 64)),
				amount: Number(account.account.data.readBigUInt64LE(64))
			});
		}

		return result;
	}

	async updateChunk(account: ProgramAccount, data: number[]): Promise<void> {
		const token = (
			await this._wallet.connection.getTokenLargestAccounts(account.owner_token)
		).value.filter((pair) => pair.uiAmount)[0].address;

		console.log(token.toBase58());

		const transaction = new Transaction({
			recentBlockhash: (await this._wallet.connection.getLatestBlockhash()).blockhash,
			feePayer: this._wallet.publicKey
		}).add(
			new TransactionInstruction({
				keys: [
					{ pubkey: account.publicKey, isSigner: false, isWritable: true },
					{ pubkey: this._wallet.publicKey, isSigner: true, isWritable: false },
					{ pubkey: token, isSigner: false, isWritable: false }
				],
				programId: this._programID,
				data: Buffer.from([1, 0, 0, 0].concat(data))
			})
		);

		const signature = await this._wallet.sendTransaction(transaction);

		console.log(`Update data signature: ${signature}`);
	}
}
