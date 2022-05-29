import { Keypair, PublicKey, SystemProgram, Transaction, TransactionInstruction } from '@solana/web3.js';

import { Buffer } from 'buffer';
import type { Wallet } from '../wallets/IWallet';
import type { ProgramAccount } from './ProgramAccount';
import type { TokenAccount } from './TokenAccount';

import { Metadata } from '@metaplex-foundation/mpl-token-metadata';
import { UpdateTask } from './UpdateTask';
import { APIController } from '../api/APIController';

export class Program {
	private _TOKEN_PROGRAM_ID = new PublicKey('TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA');
	private _BASE_ACCOUNT_SIZE = 36

	private _programID: PublicKey;
	private _wallet: Wallet;

	constructor(programID: PublicKey, wallet: Wallet) {
		this._programID = programID;
		this._wallet = wallet;
	}


	async initAccount (account: TokenAccount) : Promise<string> {
		let account_space = 32768 + this._BASE_ACCOUNT_SIZE
		let rent = await this._wallet.connection.getMinimumBalanceForRentExemption(account_space)
		let program_account = Keypair.generate();


		let transaction = new Transaction(
			{
				recentBlockhash: (await this._wallet.connection.getLatestBlockhash()).blockhash,
				feePayer: this._wallet.publicKey,
			}
		)

		transaction.add(
			SystemProgram.createAccount({
				fromPubkey: this._wallet.publicKey,
				lamports: rent,
				newAccountPubkey: program_account.publicKey,
				programId: this._programID,
				space: account_space
			})
		)
		
		if (account.nft_metadata == null)
			throw "Need nft metadata"

		let command = Buffer.alloc(8)
		command.writeUint32BE(0, 0) // command id - init
		command.writeUint32LE(parseInt(account.nft_metadata?.name.split('#')[1]), 4) // id
		
		transaction.add(
			new TransactionInstruction({
				keys: [
					{ pubkey: program_account.publicKey, isSigner: false, isWritable: true },
					{ pubkey: this._wallet.publicKey, isSigner: true, isWritable: false },
					{ pubkey: account.publicKey, isSigner: false, isWritable: false }
				],
				programId: this._programID,
				data: command
			})
		)

		transaction.sign(program_account)
		transaction = await this._wallet.signTransaction(transaction);
		

		console.log(transaction)
		let signature = await this._wallet.connection.sendRawTransaction(transaction.serialize())
		this._wallet.connection.confirmTransaction(signature)
		return signature
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
				owner_token: new PublicKey(account.account.data.slice(4, 36)),
				data: account.account.data.slice(36)
			});
		}

		console.log(result)

		return result;
	}

	/**
	 * @returns all user tokens (+ nfts)
	 */
	async getUserTokens(): Promise<TokenAccount[]> {
		const program_accounts = await this.getProgramAccounts();
		
		const result: TokenAccount[] = [];
		const accounts = (
			await this._wallet.connection.getTokenAccountsByOwner(this._wallet.publicKey, {
				programId: this._TOKEN_PROGRAM_ID
			})
		).value;

		for (const account of accounts) {
			const amount = Number(account.account.data.readBigUInt64LE(64));
			const mint = new PublicKey(account.account.data.slice(0, 32));
			const owner = new PublicKey(account.account.data.slice(32, 64))
			if (amount == 0) continue;

			result.push({
				publicKey: account.pubkey,
				mint,
				owner,
				nft_metadata: null,
				program_account: program_accounts.find((account) => account.owner_token.toBase58() == mint.toBase58()) || null,
				amount
			});
		}

		return result;
	}

	async updateChunk(account: ProgramAccount, offset: number, data: Buffer): Promise<string> {
		const token = (
			await this._wallet.connection.getTokenLargestAccounts(account.owner_token)
		).value.filter((pair) => pair.uiAmount)[0].address;

		let offset_buf = Buffer.alloc(4)
		offset_buf.writeUint32LE(offset)
		
		let buf = Buffer.from([1, 0, 0, 0].concat([...offset_buf]).concat([...data]))

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
				data: buf
			})
		)

		const signature = await this._wallet.sendTransaction(transaction);
		return signature
	}

	/**
	 * Fetching nft metadata
	 * @param program_account
	 */
	public async fetchNFTMetadata(token_account: TokenAccount): Promise<void> {
		const pda = await Metadata.getPDA(token_account.mint);
		const metadata = await Metadata.load(this._wallet.connection, pda);

		const response = await (await fetch(metadata.data.data.uri, { redirect: 'follow' })).json();

		token_account.nft_metadata = {
			name: response.name,
			image: response.image
		};
	}

	public async syncChunks (account: ProgramAccount) : Promise<UpdateTask[]> {
		let result: UpdateTask[] = []

		const response = await APIController.getRegion(account.id)
		let data  = Buffer.from(response.data.region_raw, 'base64')

		for (let i = 0; i < data.length; i++) {
			if (data[i] != account.data[i]) {
				console.log(i, data[i], account.data[i])
				const offset = Math.floor(i / 900) * 900
				result.push(new UpdateTask(this, account, offset, data.slice(offset, offset + 900)))
				i = offset + 900;
			}
		}

		return result
	}
}
