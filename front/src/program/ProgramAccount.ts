import type { PublicKey } from '@solana/web3.js';
import type { NFTMetadata } from './NFTMetadata';

export type ProgramAccount = {
	publicKey: PublicKey;

	owner_token: PublicKey;
	daddy: PublicKey;
	data: Buffer;

	nft_metadata: NFTMetadata | null;
};
