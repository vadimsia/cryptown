<script lang="ts">
	import { onMount } from 'svelte/internal';
	import { walletController } from '../store/store';
	import { Program } from '../program/program';
	import { PublicKey } from '@solana/web3.js';
	import type { TokenAccount } from 'src/program/TokenAccount';

	let regionSize = function (number: number) {
		return '' + number + 'X' + number;
	};

	const PROGRAM_ID = new PublicKey(import.meta.env.VITE_PROGRAM_ID || '4DFVfzHvvRWXUcuFSjQ5RRgn4mtkqfnJwmNNSZwKtAVC');
	const UPDATE_AUTHORITY_ID = new PublicKey(import.meta.env.VITE_UPDATE_AUTHORITY_ID || 'ECCjGknHtdDMkjn8fJ2jPKT143AxsHWzHAd2Uw3iqj4g');

	let user_tokens: TokenAccount[];
	let program = new Program(PROGRAM_ID, $walletController.wallet);

	onMount(async () => {
		loadUserTokens()
	});


	async function loadUserTokens () {
		user_tokens = []
		user_tokens = await program.getUserTokens(UPDATE_AUTHORITY_ID);
	}

	async function initRegion (token: TokenAccount) {
		let signature = await program.initAccount(token)	

		console.log(signature)

		await loadUserTokens()
	}

	async function updateChunk (token: TokenAccount) {
		alert('Wait a bit while updating chunk....')
		
		if (token.program_account) {
			await program.updateChunk(token.program_account);
			alert('Chunk updated!')
		}
		loadUserTokens()
	}

</script>

<div class="regions">
	{#if user_tokens}
		<div class="regions-box">
			{#each user_tokens as token}
				<div class="region {token.program_account ? 'active' : 'not-active'}">
					<div class="name">
						{token.nft_metadata.name}
					</div>
					<div class="pic">
						<img alt="" src={token.nft_metadata.image} width="300px" height="300px" />
					</div>
					<div class="public">
						<div class="param {token.program_account ? 'not-active-param' : 'active-param'}">
							Public Key
						</div>
						<div class="value">
							{('' + token.publicKey).substr(1, 3) +
								'...' +
								('' + token.publicKey).substr(('' + token.publicKey).length - 3, 3)}
						</div>
					</div>
					<div class="public">
						<div class="param {token.program_account ? 'not-active-param' : 'active-param'}">
							Region Size
						</div>
						<div class="value">
							{regionSize(Math.sqrt(program.calcRegionSize(token) / (64 * 2)))}
						</div>
					</div>
					{#if !token.program_account}
						<p class="init" on:click={() => initRegion(token)}>
							Initialization
						</p>
					{:else}
						<p class="update" on:click={() => updateChunk(token)}>
							Update
						</p>
					{/if}
				</div>
			{/each}
		</div>
	{:else}
		<div class="loader">
			<img alt="loader" src="/loader.svg" width="50px" height="50px" />
		</div>
	{/if}
</div>

<style>
	p {
		cursor: pointer;
	}

	.regions {
		min-width: 760px;
		flex-direction: row;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 1vw;
		background-color: #00000096;
		width: 100%;
		margin-top: 10px;
		margin-right: 10px;
		border-radius: 4px;
	}

	.regions-box {
		flex-wrap: wrap;
		display: flex;
		justify-content: center;
		flex-direction: row;
	}

	.region {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		padding: 10px;
		margin: 10px;
		border-radius: 4px;
	}

	.active {
		border: 3px solid #01ad03;
	}

	.not-active {
		border: 3px solid yellow;
	}

	.active-param {
		background-color: yellow;
	}

	.not-active-param {
		background-color: #01ad03;
		/* color: #e9e9e9e8; */
	}

	.name {
		color: #e9e9e9e8;
		margin-bottom: 10px;
	}

	.pic {
		margin-bottom: 10px;
	}

	.public {
		display: flex;
		width: 80%;
		flex-direction: row;
		justify-content: space-between;
		margin-bottom: 5px;
	}

	.param {
		border-radius: 4px;
		padding: 3px;
		/* margin-right: 5px; */
	}

	.value {
		padding: 3px;
		color: #e9e9e9e8;
	}

	.init {
		border-radius: 4px;
		padding: 15px;
		background-color: yellow;
	}

	.update {
		background-color: #01ad03;
		border-radius: 4px;
		padding: 15px;
	}
</style>
