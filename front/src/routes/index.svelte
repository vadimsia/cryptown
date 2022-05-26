<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { PublicKey } from '@solana/web3.js';
	import { APIController } from '../api/APIController';
	import { Buffer } from 'buffer'

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey('AoNiQdgpqwE1PYc5R5gYqxWv9nQtr3xN3gTEdGb4tFeW');

	let controller: IWalletController;

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		await controller.connect();
	}

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, controller.wallet);

		// Достает все участки данной программы
		//let program_accounts = await program.getProgramAccounts();

		// Достает только участки авторизованного пользователя
		let user_accounts = await program.getUserAccounts();
		console.log(user_accounts);

		// Загружает данные о нфт (картинка, название)
		await program.fetchNFTMetadata(user_accounts[0]);

		const response = await APIController.getRegion(user_accounts[0].id)
		let data  = Buffer.from(response.data.region_raw, 'base64')
		console.log(data)
		program.updateChunk(user_accounts[0], 0, data.slice(0, 32))
		

		console.log(user_accounts[0].nft_metadata); // <-- После выполнения fetchNFTMetadata здесь лежат данные о нфт, которые можно отобразить
	}
</script>

<button on:click={connectWallet}>Authorize using Phantom</button>
{#if controller}
	<button on:click={updateChunk}>Update chunk</button>
{/if}
