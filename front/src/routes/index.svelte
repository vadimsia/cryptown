<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { Program } from '../program/program';
	import { walletState } from '../store/store';
	import Wallets from '../components/wallets.svelte';
	import { Keypair, PublicKey } from '@solana/web3.js';
	import { CandyMachine } from '../nftprogram/candymachine';
	import { onMount } from 'svelte';
	import { Buffer } from 'buffer';
	import Toolbar from '../components/toolbar.svelte';

	// ID программы по маинкрафту в солане
	const PROGRAM_ID = new PublicKey('FangADZappzjG1pNsfo3zTct4AZ2VXyYq7TMfgd4YRmy');
	const CANDY_MACHINE_ID = new PublicKey('9q2vhJgPo3ZC59ctdZoQ8gq84A5YYxc7wBPGKUf2EVrF');
	const UPDATE_AUTHORITY_ID = new PublicKey('HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU');

	let controller: IWalletController;
	let loaded = false;

	onMount(() => {
		loaded = true;
		window.Buffer = Buffer;
	});

	let walletState_value: boolean;
	walletState.subscribe((value) => {
		walletState_value = value;
	});

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		try {
			await controller.connect();
		} catch (e) {}
		controller.wallet.loggedIn = controller.wallet.loggedIn; // костыль шоб было реактивно, приколы свелте... В каждой бочке меда есть ложка дегтя
	}

	async function mint() {
		let machine = new CandyMachine(CANDY_MACHINE_ID, controller.wallet);
		let account = await machine.getCandyMachineAccount();
		console.log(
			await machine.mintOneToken(account, controller.wallet.publicKey, Keypair.generate())
		);
	}

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, controller.wallet);

		// Достает все участки данной программы
		//let program_accounts = await program.getProgramAccounts();

		// Достает только участки авторизованного пользователя
		let user_tokens = await program.getUserTokens(UPDATE_AUTHORITY_ID);
		console.log(user_tokens);
		for (let token of user_tokens) {
			if (!token.program_account) {
				let signature = await program.initAccount(token);
				console.log(signature);
			} else {
				let signatures = await program.updateChunk(token.program_account);
				console.log(signatures);
			}
		}

		// controller.wallet.connection.
		// let result = await controller.wallet.signMessage("Hello world!")
		// console.log(result.signature.toString('base64'))
		// console.log(controller.wallet.publicKey.toBytes().toString('base64'))
	}
</script>

<div class="main">
	<div class={loaded ? 'container-1 done' : 'container-1'}>
		<div class="preloader">
			<img alt="loader" src="/loader.svg" width="10%" height="10%" />
		</div>
	</div>
	<div class="container-2">
		<div class="content">
			<Wallets />
			<div class="toolbar-container">
				<Toolbar />
			</div>
			{#if controller && controller.wallet.loggedIn}
				<p class="button" on:click={updateChunk}>Update chunk</p>
				<p class="button" on:click={mint}>Mint</p>
			{/if}
		</div>
	</div>
</div>

<style>
	.main {
		position: relative;
		width: 100%;
		height: 100vh;
	}

	.container-1 {
		z-index: 2;
		position: absolute;
		width: 100%;
		height: 100%;
	}

	.preloader {
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 100;
		width: 100%;
		height: 100%;
		background: rgb(164, 77, 87);
		background: -moz-linear-gradient(
			221deg,
			rgba(164, 77, 87, 1) 0%,
			rgba(104, 81, 150, 1) 50%,
			rgba(27, 85, 230, 1) 100%
		);
		background: -webkit-linear-gradient(
			221deg,
			rgba(164, 77, 87, 1) 0%,
			rgba(104, 81, 150, 1) 50%,
			rgba(27, 85, 230, 1) 100%
		);
		background: linear-gradient(
			221deg,
			rgba(164, 77, 87, 1) 0%,
			rgba(104, 81, 150, 1) 50%,
			rgba(27, 85, 230, 1) 100%
		);
	}

	.done {
		display: none;
	}

	.container-2 {
		position: absolute;
		width: 100%;
		height: 100%;
	}

	.content {
		display: flex;
		width: 100%;
		height: 100%;
		background: no-repeat url('/background.png');
		background-attachment: fixed;
		background-size: cover;
	}

	.toolbar-container {
		display: flex;
		justify-content: center;
		width: 100%;
	}
</style>
