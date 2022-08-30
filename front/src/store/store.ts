import { writable } from 'svelte/store';
import type { Writable } from 'svelte/store';
import type { IWalletController } from '../wallets/IWalletController';
import Mint from '../components/mint.svelte';
import Info from '../components/info.svelte';
import Regions from '../components/regions.svelte';
import { PhantomWallet } from '../wallets/PhantomWallet';

export interface ToolbarItem {
	id: number,
	name: string,
	state: boolean,
	walletDepends: boolean,
	component: any
}

export const toolbarItems: Writable<ToolbarItem[]> = writable([
	{ id: 0, name: 'Mint', state: true, walletDepends: false, component: Mint },
	{ id: 1, name: 'Info', state: false, walletDepends: false, component: Info },
	{ id: 2, name: 'Regions', state: false, walletDepends: true, component: Regions }
]);

export const walletState: Writable<boolean> = writable(false);

export const walletController: Writable<IWalletController> = writable(new PhantomWallet());
