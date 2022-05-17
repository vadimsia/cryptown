<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { PublicKey } from '@solana/web3.js';

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey('9sbRLyisEEu3ybKwonrfyNxQNU2NZzxApTtYJhfBmXfK');

	let controller: IWalletController;

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		await controller.connect();
	}

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, controller.wallet);

		// Достает все участки данной программы
		// let program_accounts = await program.getProgramAccounts();

		// Достает только участки авторизованного пользователя
		let user_accounts = await program.getUserAccounts();

		// Загружает данные о нфт (картинка, название)
		await program.fetchNFTMetadata(user_accounts[0]);

		user_accounts[0].nft_metadata; // <-- После выполнения fetchNFTMetadata здесь лежат данные о нфт, которые можно отобразить
	}
</script>

<button on:click={connectWallet}>Authorize using Phantom</button>
{#if controller}
	<button on:click={updateChunk}>Update chunk</button>
{/if}
