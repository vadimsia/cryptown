<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { Keypair, PublicKey } from '@solana/web3.js';
	import { CandyMachine } from '../nftprogram/candymachine';
	import { onMount } from 'svelte';
	import { Buffer } from 'buffer'

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey('BZuqbnwSbcxTM5GyDw1V1vbM7YbPqXauYRGjViBMGCor');
	const CANDY_MACHINE_ID = new PublicKey('H82W1XdstGha16QW5SaDRcer9HzEPYWEub1KfGHhGssd');
	const UPDATE_AUTHORITY_ID = new PublicKey('HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU')

	let controller: IWalletController;

	onMount (() => {
		window.Buffer = Buffer;
	})

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		await controller.connect();
	}

	async function mint() {
		let machine = new CandyMachine(CANDY_MACHINE_ID, controller.wallet)
		let account = await machine.getCandyMachineAccount()
		console.log(await machine.mintOneToken(account, controller.wallet.publicKey, Keypair.generate()))
	}

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, controller.wallet);

		// Достает все участки данной программы
		//let program_accounts = await program.getProgramAccounts();

		// Достает только участки авторизованного пользователя
		let user_tokens = await program.getUserTokens(UPDATE_AUTHORITY_ID);
		console.log(user_tokens)
		for (let token of user_tokens) {
			if (!token.program_account) {
				let signature = await program.initAccount(token);
				console.log(signature);
			} else {
				let signatures = await program.updateChunk(token.program_account);
				console.log(signatures)
			}
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
	<button on:click={mint}>Mint</button>
{/if}
