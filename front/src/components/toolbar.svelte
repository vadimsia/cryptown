<script lang="ts">
	import { walletState, walletController, toolbarItems } from '../store/store';

	function Toggle(this: any) {
		$toolbarItems.map((item) => {
			if (item.state)
				item.state = false;
		});

		$toolbarItems[this.id].state = true;
		$toolbarItems = $toolbarItems
	}
</script>

<div class="toolbar">
	<div class="toolbar-box">
		<div class="logo">
			Cryp
			<div>Town</div>
		</div>
		<div class="toolbar-items">
			{#each $toolbarItems as item}
				{#if item.walletDepends}
					{#if $walletController.wallet.loggedIn}
						<div
							class="toolbar-item {item.state ? 'active' : ''}"
							id={'' + item.id}
							on:click={Toggle}
						>
							{item.name}
						</div>
					{/if}
				{:else}
					<div
						class="toolbar-item {item.state ? 'active' : ''}"
						id={'' + item.id}
						on:click={Toggle}
					>
						{item.name}
					</div>
				{/if}
			{/each}
		</div>
		{#if !$walletController.wallet.loggedIn}
			<p class="button" id="connect-wallet" on:click={() => $walletState = true}>
				<img alt="credit card" src="/credit-card.svg" width="25px" />Connect Wallet
			</p>
		{:else}
			<div class="publick-key">
				{('' + $walletController.wallet.publicKey).substr(1, 3) +
					'...' +
					('' + $walletController.wallet.publicKey).substr(
						('' + $walletController.wallet.publicKey).length - 3,
						3
					)}
			</div>
		{/if}
	</div>
</div>

<style>
	.toolbar {
		/* min-width: 1250px; */
		display: flex;
		align-items: center;
		padding-right: 10px;
		padding-left: 10px;
		margin-top: 10px;
		width: 100%;
		height: 60px;
		background-color: #00000096;
		border-radius: 4px;
	}

	.toolbar-box {
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-direction: row;
	}

	.logo {
		font-family: 'CALVIN';
		display: flex;
		font-size: calc(1rem + 0.5vw);
		flex-direction: row;
		padding-bottom: 1px;
		margin-right: 15px;
		color: #e9e9e9e8;
	}

	.logo div {
		padding-left: 5px;
		padding-right: 5px;
		background-color: #2f83ff;
		border-radius: 4px;
	}

	.toolbar-items {
		display: flex;
		flex-direction: row;
		align-items: center;
		color: #e9e9e9e8;
	}

	.toolbar-item {
		cursor: pointer;
		display: flex;
		justify-content: center;
		align-items: center;
		height: 58px;
		padding-left: 10px;
		padding-right: 10px;
		margin-left: 5px;
		margin-right: 5px;
		border-bottom: 2px solid;
		border-color: rgb(0, 0, 0, 0);
		font-size: 12px;
	}
	.active {
		border-color: #2f83ff;
	}
	.toolbar-item:hover {
		border-color: #2f83ff;
	}

	.button {
		margin: 0;
		cursor: pointer;
		border-radius: 4px;
	}

	.publick-key {
		padding: 5px;
		border-radius: 4px;
		background-color: #01ad03;
	}

	#connect-wallet {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 160px;
		height: 40px;
		background: #2f83ff;
		font-size: 12px;
	}
	#connect-wallet img {
		margin-right: 5px;
	}
</style>
