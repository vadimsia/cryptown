import { PublicKey, Transaction, TransactionInstruction } from '@solana/web3.js';

import { Buffer } from 'buffer';
import type { Wallet } from '../wallets/IWallet';
import type { ProgramAccount } from './ProgramAccount';
import type { TokenAccount } from './TokenAccount';

import { Metadata } from '@metaplex-foundation/mpl-token-metadata';

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
				id: Buffer.from(account.account.data.slice(0, 4)).readInt32LE(),
				daddy: new PublicKey(account.account.data.slice(4, 36)),
				owner_token: new PublicKey(account.account.data.slice(36, 68)),
				data: account.account.data.slice(68),
				nft_metadata: null
			});
		}

		return result;
	}

	/**
	 * @returns all user tokens (+ nfts)
	 */
	private async getUserTokens(): Promise<TokenAccount[]> {
		const result: TokenAccount[] = [];
		const accounts = (
			await this._wallet.connection.getTokenAccountsByOwner(this._wallet.publicKey, {
				programId: this._TOKEN_PROGRAM_ID
			})
		).value;

		for (const account of accounts) {
			const amount = Number(account.account.data.readBigUInt64LE(64));
			if (amount == 0) continue;

			result.push({
				publicKey: account.pubkey,
				mint: new PublicKey(account.account.data.slice(0, 32)),
				owner: new PublicKey(account.account.data.slice(32, 64)),
				amount
			});
		}

		return result;
	}

	async getUserAccounts(): Promise<ProgramAccount[]> {
		const program_accounts = await this.getProgramAccounts();
		const user_tokens = await this.getUserTokens();

		return program_accounts.filter(
			(account) =>
				user_tokens.find(
					(user_account) => account.owner_token.toBase58() == user_account.mint.toBase58()
				) != undefined
		);
	}

	async updateChunk(account: ProgramAccount, data: number[]): Promise<void> {
		const token = (
			await this._wallet.connection.getTokenLargestAccounts(account.owner_token)
		).value.filter((pair) => pair.uiAmount)[0].address;

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

	/**
	 * Fetching nft metadata
	 * @param program_account
	 */
	public async fetchNFTMetadata(program_account: ProgramAccount): Promise<void> {
		const pda = await Metadata.getPDA(program_account.owner_token);
		const metadata = await Metadata.load(this._wallet.connection, pda);

		const response = await (await fetch(metadata.data.data.uri, { redirect: 'follow' })).json();

		program_account.nft_metadata = {
			name: response.name,
			image: response.image
		};
	}
}
