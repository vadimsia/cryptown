<script lang="ts">
	import { CandyMachine } from '../nftprogram/candymachine';
	import { Keypair, PublicKey } from '@solana/web3.js';
	import { walletController } from '../store/store';
	import { onMount } from 'svelte';
	import type { CandyMachineAccount } from '../nftprogram/interfaces';

	const CANDY_MACHINE_ID = new PublicKey('89EXeeTSy6nxM89vhpiLw1mVta6LWY3FGkjZ6xEwuNbe');

	let machine: CandyMachine;
	let account: CandyMachineAccount;
	let time_offset = 0;

	onMount(async () => {
		machine = new CandyMachine(CANDY_MACHINE_ID, $walletController.wallet);
		account = await machine.getCandyMachineAccount();

		console.log(parseInt(account.state.goLiveDate));
		if (account.state.goLiveDate > new Date().getTime()) {
			time_offset = account.state.goLiveDate - new Date().getTime() / 1000;
			let inverval = setInterval(() => {
				if (time_offset <= 0) {
					clearInterval(inverval);
					time_offset = 0;
				}
				time_offset -= 1000;
			}, 1000);
		}
	});

	$: countdown_days = Math.floor(time_offset / (1000 * 60 * 60 * 24));
	$: countdown_hours = Math.floor((time_offset % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	$: countdown_minutes = Math.floor((time_offset % (1000 * 60 * 60)) / (1000 * 60));
	$: countdown_secundes = Math.floor((time_offset % (1000 * 60)) / 1000);

	async function mint() {
		machine = new CandyMachine(CANDY_MACHINE_ID, $walletController.wallet);
		account = await machine.getCandyMachineAccount();

		console.log(
			await machine.mintOneToken(account, $walletController.wallet.publicKey, Keypair.generate())
		);
	}
</script>

<div class="mint">
	<div class="regions">
		<div id="s1">
			<div class="area">16X16</div>
			<img alt="" src="/16.gif" width="100%" />
		</div>
		<div id="s2">
			<div class="area">76X76</div>
			<img alt="" src="/76.gif" width="100%" />
		</div>
		<div id="s3">
			<div class="area">38X38</div>
			<img alt="" src="/38.gif" width="100%" />
		</div>
	</div>
	{#if $walletController && time_offset == 0}
		<div class="button">
			<div class="solana"><img alt="solana" src="/solana-logo.svg" height="15px" /></div>
			<div class="name" on:click={mint}>Mint</div>
		</div>
	{:else if !$walletController && time_offset == 0}
		<div class="timer">Connect Wallet First</div>
	{:else}
		<div class="timer">
			{countdown_days}d {countdown_hours}h {countdown_minutes}m {countdown_secundes}s
		</div>
	{/if}
</div>

<style>
	.mint {
		min-width: 760px;
		flex-direction: column;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 15px;
		background-color: #00000096;
		width: 100%;
		margin-top: 10px;
		margin-right: 10px;
		border-radius: 4px;
	}

	.regions {
		display: flex;
		justify-content: center;
		flex-direction: row;
		padding: 40px;
	}

	.area {
		font-family: 'CALVIN';
		width: 3.5vw;
		border-radius: 4px;
		padding: 5px;
		text-align: center;
		font-size: 0.7vw;
		margin-bottom: 5px;
	}

	.regions img {
		width: 17vw;
		min-width: 200px;
	}

	#s1 {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		border: 3px solid #01ad03;
		border-radius: 4px;
		padding: 10px;
		margin-right: 2.5vw;
	}

	#s1 .area {
		color: #01ad03;
		border: 3px solid #01ad03;
	}

	#s1:hover {
		box-shadow: 0px 0px 100px #01ad03;
	}

	#s2 {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		border: 3px solid #2f83ff;
		border-radius: 4px;
		padding: 10px;
	}

	#s2 .area {
		color: #2f83ff;
		border: 3px solid #2f83ff;
	}

	#s2:hover {
		box-shadow: 0px 0px 100px #2f83ff;
	}

	#s3 {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		border: 3px solid rgb(173, 12, 1);
		border-radius: 4px;
		padding: 10px;
		margin-left: 2.5vw;
	}

	#s3 .area {
		color: rgb(173, 12, 1);
		border: 3px solid rgb(173, 12, 1);
	}

	#s3:hover {
		box-shadow: 0px 0px 100px rgb(173, 12, 1);
	}

	.button {
		border-radius: 4px;
		display: flex;
		flex-direction: row;
		padding: 10px;
		cursor: pointer;
		background-color: #2f83ff;
	}

	.solana {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-right: 4px;
	}

	.timer {
		color: #2f83ff;
	}

	.name {
		text-align: center;
	}
</style>
