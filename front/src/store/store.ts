import { writable } from 'svelte/store';
import type { Writable } from 'svelte/store';
import type { IWalletController } from '../wallets/IWalletController';
import type { Wallet } from '../wallets/IWallet';
export const toolbarItems: Writable<Object[]> = writable([
	{ id: 0, name: 'Mint', state: true, walletDepends: false },
	{ id: 1, name: 'Regions', state: false, walletDepends: true },
	{ id: 2, name: 'Info', state: false, walletDepends: false }
]);
export const walletState: Writable<boolean> = writable(false);
export const walletController: Writable<IWalletController | null> = writable(null);
