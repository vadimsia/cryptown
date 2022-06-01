<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { PublicKey } from '@solana/web3.js';

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey('u35VEZ9gPkPg1VAp3YAxPejRhKKu5q8FJagEc7vUs6Y');

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
		let user_tokens = await program.getUserTokens();
		// console.log(user_tokens);
		await program.fetchNFTMetadata(user_tokens[0])
		if (!user_tokens[0].program_account) {
			let signature = await program.initAccount(user_tokens[0])
			console.log(signature)
		} else {

			let tasks = await program.syncChunks(user_tokens[0].program_account)
			for (let task of tasks)
				console.log(await task.execute())
		}

		// controller.wallet.connection.
		// let result = await controller.wallet.signMessage("Hello world!")
		// console.log(result.signature.toString('base64'))
		// console.log(controller.wallet.publicKey.toBytes().toString('base64'))
	}
</script>

<button on:click={connectWallet}>Authorize using Phantom</button>
{#if controller}
	<button on:click={updateChunk}>Update chunk</button>
{/if}
