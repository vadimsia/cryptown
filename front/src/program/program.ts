import {
	Keypair,
	PublicKey,
	SystemProgram,
	Transaction,
	TransactionInstruction
} from '@solana/web3.js';

import { Buffer } from 'buffer';
import type { Wallet } from '../wallets/IWallet';
import type { ProgramAccount } from './ProgramAccount';
import type { TokenAccount } from './TokenAccount';

import { Metadata } from '@metaplex-foundation/mpl-token-metadata';
import type { UpdateTask } from './UpdateTask';
import { APIController } from '../api/APIController';
import type { NFTMetadata } from './NFTMetadata';

export class Program {
	private _TOKEN_PROGRAM_ID = new PublicKey('TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA');
	private _BASE_ACCOUNT_SIZE = 36;

	private _programID: PublicKey;
	private _wallet: Wallet;

	constructor(programID: PublicKey, wallet: Wallet) {
		this._programID = programID;
		this._wallet = wallet;
	}

	async initAccount(account: TokenAccount): Promise<string> {
		let account_space = 32768 + this._BASE_ACCOUNT_SIZE;
		let rent = await this._wallet.connection.getMinimumBalanceForRentExemption(account_space);
		let program_account = Keypair.generate();

		let transaction = new Transaction({
			recentBlockhash: (await this._wallet.connection.getLatestBlockhash()).blockhash,
			feePayer: this._wallet.publicKey
		});

		transaction.add(
			SystemProgram.createAccount({
				fromPubkey: this._wallet.publicKey,
				lamports: rent,
				newAccountPubkey: program_account.publicKey,
				programId: this._programID,
				space: account_space
			})
		);

		if (account.program_account != null) throw 'Already initialized';

		if (account.nft_metadata == null) throw 'Need nft metadata';

		transaction.add(
			new TransactionInstruction({
				keys: [
					{ pubkey: program_account.publicKey, isSigner: false, isWritable: true },
					{ pubkey: this._wallet.publicKey, isSigner: true, isWritable: false },
					{ pubkey: await Metadata.getPDA(account.mint), isSigner: false, isWritable: false }
				],
				programId: this._programID,
				data: Buffer.from([0, 0, 0, 0])
			})
		);

		transaction.sign(program_account);
		transaction = await this._wallet.signTransaction(transaction);

		let signature = await this._wallet.connection.sendRawTransaction(transaction.serialize());
		this._wallet.connection.confirmTransaction(signature);
		return signature;
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
				id: Buffer.from(account.account.data.slice(0, 4)).readInt32BE(),
				owner_token: new PublicKey(account.account.data.slice(4, 36)),
				data: account.account.data.slice(36)
			});
		}

		return result;
	}

	/**
	 * @returns all user tokens (+ nfts)
	 */
	async getUserTokens(updateAuthority: PublicKey): Promise<TokenAccount[]> {
		const program_accounts = await this.getProgramAccounts();

		const result: TokenAccount[] = [];
		const accounts = (
			await this._wallet.connection.getTokenAccountsByOwner(this._wallet.publicKey, {
				programId: this._TOKEN_PROGRAM_ID
			})
		).value;
		console.log(accounts)
		for (const account of accounts) {
			const amount = Number(account.account.data.readBigUInt64LE(64));
			const mint = new PublicKey(account.account.data.slice(0, 32));
			const owner = new PublicKey(account.account.data.slice(32, 64));
			const program_account = program_accounts.find((account) => account.owner_token.toBase58() == mint.toBase58()) || null

			if (amount != 1) continue

			try {
				result.push({
					publicKey: account.pubkey,
					mint,
					owner,
					nft_metadata: await this.fetchNFTMetadata(mint),
					program_account,
					amount
				});
			} catch (e) {
				console.log(e);
				continue;
			}
		}

		return result.filter((account) => account.nft_metadata.creator.toBase58() == updateAuthority.toBase58());
	}

	async updateChunk(account: ProgramAccount): Promise<string[]> {
		const token = (
			await this._wallet.connection.getTokenLargestAccounts(account.owner_token)
		).value.filter((pair) => pair.uiAmount)[0].address;

		const blockhash = (await this._wallet.connection.getLatestBlockhash()).blockhash;
		const tasks = await this.syncChunks(account);

		if (tasks.length == 0) {
			console.log(`${account.publicKey.toBase58()} already has actual data`)
			return [];
		}


		let promises: Promise<string>[] = []
		let signatures: string[] = []
		let transactions: Transaction[] = []

		for (let task of tasks)
			transactions.push(this.updateChunkTransaction(account, task, token, blockhash));

		const signed = await this._wallet.signAllTransactions(transactions)

		for (let transaction of signed)
			promises.push(this._wallet.sendRawTransaction(transaction))

		for (let promise of promises) {
			try {
				signatures.push(await promise)
			} catch (e) {
				console.log(`Error ${e} while processing transaction`)
				signatures.push('')
			}
		}
		
		return signatures;
	}

	private updateChunkTransaction(account: ProgramAccount, task: UpdateTask, token: PublicKey, blockhash: string): Transaction {

		let offset_buf = Buffer.alloc(4);
		offset_buf.writeUint32LE(task.offset);

		let buf = Buffer.from([1, 0, 0, 0].concat([...offset_buf]).concat([...task.data]));

		return new Transaction({
			recentBlockhash: blockhash,
			feePayer: this._wallet.publicKey
		}).add(
			new TransactionInstruction({
				keys: [
					{ pubkey: account.publicKey, isSigner: false, isWritable: true },
					{ pubkey: this._wallet.publicKey, isSigner: true, isWritable: false },
					{ pubkey: token, isSigner: false, isWritable: false }
				],
				programId: this._programID,
				data: buf
			})
		);
	}

	/**
	 * Fetching nft metadata
	 * @param program_account
	 */
	private async fetchNFTMetadata(mint: PublicKey): Promise<NFTMetadata> {
		const pda = await Metadata.getPDA(mint);
		const metadata = await Metadata.load(this._wallet.connection, pda);
		const response = await (await fetch(metadata.data.data.uri, { redirect: 'follow' })).json();

		return {
			name: response.name,
			image: response.image,
			creator: new PublicKey(metadata.data.updateAuthority)
		};
	}

	private async syncChunks(account: ProgramAccount): Promise<UpdateTask[]> {
		let result: UpdateTask[] = [];

		const response = await APIController.getRegion(account.id);
		let data = Buffer.from(response.data.region_raw, 'base64');

		for (let i = 0; i < data.length; i++) {
			if (data[i] != account.data[i]) {
				console.log(i, data[i], account.data[i]);
				const offset = Math.floor(i / 900) * 900;
				result.push({ offset: offset, data: data.slice(offset, offset + 900) });
				i = offset + 900;
			}
		}

		return result;
	}
}
