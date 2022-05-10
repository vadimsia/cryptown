import type { PublicKey } from '@solana/web3.js';

export type TokenAccount = {
	publicKey: PublicKey;
	mint: PublicKey;
	owner: PublicKey;
	amount: number;
};
