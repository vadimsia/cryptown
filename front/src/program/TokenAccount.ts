import type { PublicKey } from '@solana/web3.js';
import type { NFTMetadata } from './NFTMetadata';
import type { ProgramAccount } from './ProgramAccount';

export type TokenAccount = {
	publicKey: PublicKey;
	mint: PublicKey;
	owner: PublicKey;
	amount: number;

	nft_metadata: NFTMetadata | null;
	program_account: ProgramAccount | null;
};
