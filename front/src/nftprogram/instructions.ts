import anchor from '@project-serum/anchor';
import { TOKEN_PROGRAM_ID } from '@solana/spl-token';
import { SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID } from './programs';

export function createAssociatedTokenAccountInstruction(
	associatedTokenAddress: anchor.web3.PublicKey,
	payer: anchor.web3.PublicKey,
	walletAddress: anchor.web3.PublicKey,
	splTokenMintAddress: anchor.web3.PublicKey
) {
	const keys = [
		{ pubkey: payer, isSigner: true, isWritable: true },
		{ pubkey: associatedTokenAddress, isSigner: false, isWritable: true },
		{ pubkey: walletAddress, isSigner: false, isWritable: false },
		{ pubkey: splTokenMintAddress, isSigner: false, isWritable: false },
		{
			pubkey: anchor.web3.SystemProgram.programId,
			isSigner: false,
			isWritable: false
		},
		{ pubkey: TOKEN_PROGRAM_ID, isSigner: false, isWritable: false },
		{
			pubkey: anchor.web3.SYSVAR_RENT_PUBKEY,
			isSigner: false,
			isWritable: false
		}
	];

	return new anchor.web3.TransactionInstruction({
		keys,
		programId: SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID,
		data: Buffer.from([])
	});
}
