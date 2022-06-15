import * as anchor from '@project-serum/anchor';
import { SystemProgram, Transaction } from '@solana/web3.js';
import type { Wallet } from 'src/wallets/IWallet';
import type { CandyMachineAccount } from './interfaces';

import {
	CANDY_MACHINE_PROGRAM,
	SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID,
	TOKEN_METADATA_PROGRAM_ID
} from './programs';
import { TOKEN_PROGRAM_ID, MintLayout, createMintToInstruction, createInitializeMintInstruction } from '@solana/spl-token';
import { createAssociatedTokenAccountInstruction } from './instructions';

export class CandyMachine {
	private machineID: anchor.web3.PublicKey;
	private provider: anchor.Provider;

	constructor(machineID: anchor.web3.PublicKey, wallet: Wallet) {
		this.machineID = machineID;
		this.provider = new anchor.Provider(
			wallet.connection,
			wallet as unknown as anchor.Wallet,
			{ preflightCommitment: 'processed' } as anchor.web3.ConfirmOptions
		);
	}

	private async getCandyMachineMetadata(
		program: anchor.Program<anchor.Idl>,
		candyMachineId: anchor.web3.PublicKey
	) {
		const state: any = await program.account.candyMachine.fetch(candyMachineId);
		const itemsAvailable = state.data.itemsAvailable.toNumber();
		const itemsRedeemed = state.itemsRedeemed.toNumber();
		const itemsRemaining = itemsAvailable - itemsRedeemed;

		return {
			id: candyMachineId,
			program,
			state: {
				itemsAvailable,
				itemsRedeemed,
				itemsRemaining,
				isSoldOut: itemsRemaining === 0,
				isActive:
					state.data.goLiveDate.toNumber() < new Date().getTime() / 1000 &&
					(state.endSettings
						? state.endSettings.endSettingType.date
							? state.endSettings.number.toNumber() > new Date().getTime() / 1000
							: itemsRedeemed < state.endSettings.number.toNumber()
						: true),
				goLiveDate: state.data.goLiveDate,
				treasury: state.wallet,
				tokenMint: state.tokenMint,
				gatekeeper: state.data.gatekeeper,
				endSettings: state.data.endSettings,
				whitelistMintSettings: state.data.whitelistMintSettings,
				hiddenSettings: state.data.hiddenSettings,
				price: state.data.price
			}
		};
	}

	private async getAtaForMint(mint: anchor.web3.PublicKey, buyer: anchor.web3.PublicKey): Promise<anchor.web3.PublicKey> {
		let address = await anchor.web3.PublicKey.findProgramAddress(
			[buyer.toBuffer(), TOKEN_PROGRAM_ID.toBuffer(), mint.toBuffer()],
			SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID
		);

		return address[0];
	}

	private async getMasterEdition(mint: anchor.web3.PublicKey): Promise<anchor.web3.PublicKey> {
		const address = await anchor.web3.PublicKey.findProgramAddress(
			[
				Buffer.from('metadata'),
				TOKEN_METADATA_PROGRAM_ID.toBuffer(),
				mint.toBuffer(),
				Buffer.from('edition')
			],
			TOKEN_METADATA_PROGRAM_ID
		);

		return address[0];
	}

	private async getMetadata(mint: anchor.web3.PublicKey): Promise<anchor.web3.PublicKey> {
		let address = await anchor.web3.PublicKey.findProgramAddress(
			[Buffer.from('metadata'), TOKEN_METADATA_PROGRAM_ID.toBuffer(), mint.toBuffer()],
			TOKEN_METADATA_PROGRAM_ID
		);

		return address[0];
	}

	private async getCandyMachineCreator(
		candyMachine: anchor.web3.PublicKey
	): Promise<[anchor.web3.PublicKey, number]> {
		let address = await anchor.web3.PublicKey.findProgramAddress(
			[Buffer.from('candy_machine'), candyMachine.toBuffer()],
			CANDY_MACHINE_PROGRAM
		);

		return address;
	}

	private async getCollectionPDA (candyMachineAddress: anchor.web3.PublicKey): Promise<anchor.web3.PublicKey> {
		const address = await anchor.web3.PublicKey.findProgramAddress(
		  [Buffer.from('collection'), candyMachineAddress.toBuffer()],
		  CANDY_MACHINE_PROGRAM,
		);

		return address[0];
	  };

	public async getCandyMachineAccount(): Promise<CandyMachineAccount> {
		const idl = await anchor.Program.fetchIdl(CANDY_MACHINE_PROGRAM, this.provider);
		if (idl) {
			const program = new anchor.Program(idl, CANDY_MACHINE_PROGRAM, this.provider);
			return await this.getCandyMachineMetadata(program, this.machineID);
		} else throw 'Cant fetch idl';
	}

	private async getCollectionAuthorityRecordPDA(mint: anchor.web3.PublicKey, newAuthority: anchor.web3.PublicKey): Promise<anchor.web3.PublicKey> {
		let address = await anchor.web3.PublicKey.findProgramAddress(
		  [
			Buffer.from('metadata'),
			TOKEN_METADATA_PROGRAM_ID.toBuffer(),
			mint.toBuffer(),
			Buffer.from('collection_authority'),
			newAuthority.toBuffer(),
		  ],
		  TOKEN_METADATA_PROGRAM_ID,
		);

		return address[0];
	  };

	public async mintOneToken(
		candyMachine: CandyMachineAccount,
		payer: anchor.web3.PublicKey,
		mint: anchor.web3.Keypair
	): Promise<string> {
		const userTokenAccountAddress = await this.getAtaForMint(mint.publicKey, payer);
		const candyMachineAddress = candyMachine.id;
		const signers: anchor.web3.Keypair[] = [mint];
		const metadataAddress = await this.getMetadata(mint.publicKey);
		const masterEdition = await this.getMasterEdition(mint.publicKey);
		const [candyMachineCreator, creatorBump] = await this.getCandyMachineCreator(candyMachineAddress);
		const connection = candyMachine.program.provider.connection;
		const wallet = candyMachine.program.provider.wallet;

		const collectionPDA = await this.getCollectionPDA(candyMachineAddress);
		const collectionPDAAccount = await this.provider.connection.getAccountInfo(collectionPDA);
	
		const collectionPdaData = (await candyMachine.program.account.collectionPda.fetch(collectionPDA)) as { mint: anchor.web3.PublicKey; };
		const collectionMint = collectionPdaData.mint;
		const collectionAuthorityRecord = await this.getCollectionAuthorityRecordPDA(collectionMint, collectionPDA)

		const collectionMetadata = await this.getMetadata(collectionMint);
		const collectionMasterEdition = await this.getMasterEdition(collectionMint);

		const instructions = [
			anchor.web3.SystemProgram.createAccount({
				fromPubkey: payer,
				newAccountPubkey: mint.publicKey,
				space: MintLayout.span,
				lamports: await connection.getMinimumBalanceForRentExemption(MintLayout.span),
				programId: TOKEN_PROGRAM_ID
			}),
			createInitializeMintInstruction(mint.publicKey, 0, payer, null, TOKEN_PROGRAM_ID),
			createAssociatedTokenAccountInstruction(
				userTokenAccountAddress,
				payer,
				payer,
				mint.publicKey
			),
			createMintToInstruction(
				mint.publicKey,
				userTokenAccountAddress,
				payer,
				1,
				[],
				TOKEN_PROGRAM_ID
			),
            candyMachine.program.instruction.mintNft(creatorBump, {
				accounts: {
					candyMachine: candyMachineAddress,
					candyMachineCreator,
					payer: payer,
					wallet: candyMachine.state.treasury,
					mint: mint.publicKey,
					metadata: metadataAddress,
					masterEdition,
					mintAuthority: payer,
					updateAuthority: payer,
					tokenMetadataProgram: TOKEN_METADATA_PROGRAM_ID,
					tokenProgram: TOKEN_PROGRAM_ID,
					systemProgram: SystemProgram.programId,
					rent: anchor.web3.SYSVAR_RENT_PUBKEY,
					clock: anchor.web3.SYSVAR_CLOCK_PUBKEY,
					recentBlockhashes: anchor.web3.SYSVAR_SLOT_HASHES_PUBKEY,
					instructionSysvarAccount: anchor.web3.SYSVAR_INSTRUCTIONS_PUBKEY
				},
				remainingAccounts: undefined
			}),
			candyMachine.program.instruction.setCollectionDuringMint({
				accounts: {
					candyMachine: candyMachineAddress,
					metadata: metadataAddress,
					payer: payer,
					collectionPda: collectionPDA,
					tokenMetadataProgram: TOKEN_METADATA_PROGRAM_ID,
					instructions: anchor.web3.SYSVAR_INSTRUCTIONS_PUBKEY,
					collectionMint: collectionMint,
					collectionMetadata,
					collectionMasterEdition,
					authority: candyMachine.state.treasury,
					collectionAuthorityRecord,
				},
			})
		];

		console.log(instructions)

		let transaction = new Transaction({
			recentBlockhash: (await connection.getLatestBlockhash()).blockhash,
			feePayer: wallet.publicKey
		}).add(...instructions);

		transaction.sign(...signers);

		const tx = await wallet.signTransaction(transaction);

		const signature = await connection.sendRawTransaction(tx.serialize());
		await connection.confirmTransaction(signature);

		return signature;
	}
}
