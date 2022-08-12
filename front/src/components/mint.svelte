<script lang="ts">
	import { CandyMachine } from '../nftprogram/candymachine';
	import { Connection, Keypair, PublicKey } from '@solana/web3.js';
	import { walletController } from '../store/store';
	import { onMount } from 'svelte';
	import type { CandyMachineAccount } from 'src/nftprogram/interfaces';
	import { PhantomWallet } from '../wallets/PhantomWallet';


	const CANDY_MACHINE_ID = new PublicKey('9q2vhJgPo3ZC59ctdZoQ8gq84A5YYxc7wBPGKUf2EVrF');
	
	let machine: CandyMachine;
	let account: CandyMachineAccount;
	let time_offset = 0;
	onMount(async () => {
		let wal = new PhantomWallet();
		machine = new CandyMachine(CANDY_MACHINE_ID, wal.wallet);
		account = await machine.getCandyMachineAccount();
		console.log(account.state.goLiveDate);
		if(account.state.goLiveDate > new Date().getTime()) {
			time_offset =account.state.goLiveDate - (new Date().getTime()/ 1000);
			let inverval = setInterval(() => {
				if (time_offset <= 0) {
						clearInterval(inverval);
						time_offset = 0;
					} 
				time_offset -= 1000;
			}, 1000)
		}
	});



	$: countdown_days = Math.floor(time_offset / (1000 * 60 * 60 * 24));
	$: countdown_hours = Math.floor((time_offset % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	$: countdown_minutes = Math.floor((time_offset % (1000 * 60 * 60)) / (1000 * 60));
	$: countdown_secundes = Math.floor((time_offset % (1000 * 60)) / 1000);

	async function mint() {
		console.log(
			await machine.mintOneToken(
				account,
				$walletController?.wallet.publicKey,
				Keypair.generate()
			)
		);
	}
</script>

<div class="mint">
	<div class="regions">
		<div id="s1">
			<div class="area">16X16</div>
			<img alt="" src="/animation.gif" />
		</div>
		<div id="s2">
			<div class="area">76X76</div>
			<img alt="" src="/animation.gif" />
		</div>
		<div id="s3">
			<div class="area">38X38</div>
			<img alt="" src="/animation.gif" />
		</div>
	</div>
	{#if $walletController && time_offset == 0}
		<div class="button">
			<div class="solana"><img alt="solana" src="/solana-logo.svg" height="15px" /></div>
			<div class="name" on:click={mint}>Mint</div>
		</div>
	{:else}
		{#if !$walletController && time_offset == 0}
			<div class="timer">
				Connect Wallet First
			</div>
		{:else}
			<div class="timer">
				{countdown_days}d {countdown_hours}h {countdown_minutes}m {countdown_secundes}s
			</div>
		{/if}
	{/if}
</div>

<style>
	.mint {
		min-width: 760px;
		flex-direction: column;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 30px;
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
		width: 10vw;
		min-width: 140px;
	}

	#s1 {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		border: 3px solid #01ad03;
		margin-top: 0.9vw;
		border-radius: 4px;
		padding: 10px;
		margin-right: 2.5vw;
		transform: perspective(1000px) rotateY(-25deg);
	}

	#s1 .area {
		color: #01ad03;
		border: 3px solid #01ad03;
	}

	#s1:hover {
		transform: perspective(1000px) rotateY(-25deg) scale(1.1);
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

	#s2 img {
		width: 13vw;
		min-width: 160px;
	}

	#s2:hover {
		box-shadow: 0px 0px 100px #2f83ff;
		transform: scale(1.1);
	}

	#s3 {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		margin-top: 0.9vw;
		border: 3px solid rgb(173, 12, 1);
		border-radius: 4px;
		padding: 10px;
		margin-left: 2.5vw;
		transform: perspective(1000px) rotateY(25deg);
	}

	#s3 .area {
		color: rgb(173, 12, 1);
		border: 3px solid rgb(173, 12, 1);
	}

	#s3:hover {
		transform: perspective(1000px) rotateY(25deg) scale(1.1);
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
