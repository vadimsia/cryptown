<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { PublicKey } from '@solana/web3.js';

	const PROGRAM_ID = new PublicKey('9sbRLyisEEu3ybKwonrfyNxQNU2NZzxApTtYJhfBmXfK');

	let controller: IWalletController;

	async function connectWallet() {
		controller = new PhantomWallet();
		await controller.connect();
	}

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, controller.wallet);

		await program.updateChunk((await program.getProgramAccounts())[0], [0, 1, 2, 3, 4, 5, 4, 3]);
	}
</script>

<button on:click={connectWallet}>Authorize using Phantom</button>
{#if controller}
	<button on:click={updateChunk}>Update chunk</button>
{/if}
