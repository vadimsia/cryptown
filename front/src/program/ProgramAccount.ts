import type { PublicKey } from '@solana/web3.js';

export type ProgramAccount = {
	publicKey: PublicKey;

	owner_token: PublicKey;
	daddy: PublicKey;
	data: Buffer;
};
