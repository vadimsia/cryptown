<script lang="ts">
	import { PhantomWallet, type SignedMessage } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';

	import { onMount } from 'svelte';
	import { Buffer } from 'buffer';
	import { APIController } from '../api/APIController';
	import { Wallet } from '@project-serum/anchor';

	let controller: IWalletController;
	let nick: string | null;
	let uuid: string | null;
	let urlParams;

	onMount(async () => {
		window.Buffer = Buffer;
		await connectWallet();
	});

	let result: SignedMessage;
	// Подключение кошелька
	async function connectWallet() {
		let queryString = window.location.search;
		urlParams = new URLSearchParams(queryString);
		nick = urlParams.get('nick');
		uuid = urlParams.get('uuid');
		console.log(urlParams.get('nick'));
		controller = new PhantomWallet();
		try {
			await controller.connect();
		} catch (e) {}
		controller.wallet.loggedIn = controller.wallet.loggedIn;

		if (!uuid) throw 'undefined uuid!';

		result = await controller.wallet.signMessage(uuid);
		console.log(result);
		let signature = result.signature.toString('base64');
		let pk = controller.wallet.publicKey.toBytes().toString('base64');

		console.log(await APIController.makeLogin(uuid, pk, signature));
	}
</script>

<div class="login">
	{#if uuid}
		<div class="loader">
			{#if !result}
				<img alt="loader" src="/loader.svg" width="60px" height="60px" />
			{:else}
				<img alt="loader" src="/check.svg" width="60px" height="60px" />
			{/if}
		</div>
		<div class="nick">
			<div class="name">Nick:</div>
			<div class="value">{nick}</div>
		</div>
		<div class="uuid">
			<div class="name">UUID:</div>
			<div class="value">{uuid}</div>
		</div>
		{#if result}
			Close this tab.
		{/if}
	{:else}
		<div class="error">
			<div class="name">Error:</div>
			<div class="value">undefined UUID.</div>
		</div>
	{/if}
</div>

<style>
	.login {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		width: 100%;
		height: 100vh;
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

	.loader {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 10px;
	}

	.nick {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: row;
		margin: 5px;
	}

	.uuid {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: row;
		margin-bottom: 20px;
	}

	.name {
		padding: 4px;
		border-radius: 4px;
		background-color: rgb(1, 173, 3);
		/* color: #e9e9e9e8; */
		margin-right: 10px;
	}

	.error {
		display: flex;
		justify-content: center;
		flex-direction: row;
		text-align: center;
	}
	.error .name {
		background-color: rgb(173, 12, 1);
	}

	.error .value {
		padding: 4px;
	}
</style>
