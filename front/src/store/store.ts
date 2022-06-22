import { writable } from 'svelte/store';
import type { Writable } from 'svelte/store';
import type { IWalletController } from '../wallets/IWalletController';
import type { Wallet } from '../wallets/IWallet';

export const walletState: Writable<boolean> = writable(false);
export const walletController: Writable<IWalletController | null> = writable(null);
