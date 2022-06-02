import type { PublicKey } from "@solana/web3.js";

export type NFTMetadata = {
	name: string;
	image: string;
	creator: PublicKey;
};
