<script lang="ts">
	import { each, onMount } from 'svelte/internal';
	import { walletController } from '../store/store';
	import { Program } from '../program/program';
	import type { IWalletController } from '../wallets/IWalletController';
	import { PublicKey } from '@solana/web3.js';
	import type { TokenAccount } from 'src/program/TokenAccount';
	import { publicKey, token } from '@project-serum/anchor/dist/cjs/utils';

	let walletController_value: IWalletController | null;
	walletController.subscribe((value) => {
		walletController_value = value;
	});

	onMount(() => {
		updateChunk();
	});

	const PROGRAM_ID = new PublicKey('FangADZappzjG1pNsfo3zTct4AZ2VXyYq7TMfgd4YRmy');
	const UPDATE_AUTHORITY_ID = new PublicKey('HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU');

	let user_tokens: TokenAccount[];
	let program = new Program(PROGRAM_ID, walletController_value?.wallet);

	async function updateChunk() {
		let program = new Program(PROGRAM_ID, walletController_value?.wallet);

		// Достает все участки данной программы
		//let program_accounts = await program.getProgramAccounts();

		// Достает только участки авторизованного пользователя
		user_tokens = await program.getUserTokens(UPDATE_AUTHORITY_ID);
		// console.log(user_tokens);
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
					{#if !token.program_account}
						<p
							class="init"
							on:click={async () => {
								await program.initAccount(token);
							}}
						>
							Initialization
						</p>
					{:else}
						<p
							class="update"
							on:click={async () => {
								await program.updateChunk(token.program_account);
							}}
						>
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
		flex-direction: row;
	}

	.param {
		border-radius: 4px;
		padding: 3px;
		margin-right: 5px;
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
