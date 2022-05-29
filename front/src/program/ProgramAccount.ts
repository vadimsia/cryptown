import type { PublicKey } from '@solana/web3.js';

export type ProgramAccount = {
	publicKey: PublicKey;

	id: number;
	owner_token: PublicKey;
	data: Buffer;
};
