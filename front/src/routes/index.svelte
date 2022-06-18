<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';

	import { Keypair, PublicKey } from '@solana/web3.js';
	import { CandyMachine } from '../nftprogram/candymachine';
	import { onMount } from 'svelte';
	import { Buffer } from 'buffer'

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey(import.meta.env.VITE_PROGRAM_ID || 'FangADZappzjG1pNsfo3zTct4AZ2VXyYq7TMfgd4YRmy');
	const CANDY_MACHINE_ID = new PublicKey(import.meta.env.VITE_CANDY_MACHINE_ID || '9q2vhJgPo3ZC59ctdZoQ8gq84A5YYxc7wBPGKUf2EVrF');
	const UPDATE_AUTHORITY_ID = new PublicKey(import.meta.env.VITE_UPDATE_AUTHORITY_ID || 'HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU')

	let controller: IWalletController;

	onMount (() => {
		window.Buffer = Buffer;
	})

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		try {
			await controller.connect();
		} catch (e) {}
		controller.wallet.loggedIn = controller.wallet.loggedIn; // костыль шоб было реактивно, приколы свелте... В каждой бочке меда есть ложка дегтя
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
{#if controller && controller.wallet.loggedIn}
	<button on:click={updateChunk}>Update chunk</button>
	<button on:click={mint}>Mint</button>
{/if}
