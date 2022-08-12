<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';
	import { walletState, walletController } from '../store/store';

	let walletState_value: boolean;
	walletState.subscribe((value) => {
		walletState_value = value;
	});

	function Close() {
		walletState.set(false);
	}

	let wallets = [
		{
			name: 'Phantom',
			logo: '/phantom.svg',
			wallet() {
				let controller: IWalletController;
				controller = new PhantomWallet();
				return controller;
			}
		}
	];

	async function connectWallet(walletItem: IWalletController) {
		let controller = walletItem;
		try {
			await controller.connect();
		} catch (e) {}
		controller.wallet.loggedIn = controller.wallet.loggedIn; // костыль шоб было реактивно, приколы свелте... В каждой бочке меда есть ложка дегтя
		if (controller.wallet.loggedIn) {
			walletController.set(controller);
			Close();
		}
	}
</script>

<div class="wallets {walletState_value ? '' : 'hide'}">
	<div class="wallets-container">
		<div class="top">
			<div class="name">Select your wallet</div>
			<div class="close" on:click={Close}>
				<img alt="" src="/close.svg" width="25px" height="25px" />
			</div>
		</div>
		<div class="wallets-list">
			{#each wallets as wallet}
				<div class="item" on:click={() => connectWallet(wallet.wallet())}>
					<div class="item-name">
						{wallet.name}
					</div>
					<div class="item-logo">
						<img src={wallet.logo} alt="" width="30px" height="30px" />
					</div>
				</div>
			{/each}
		</div>
	</div>
</div>

<style>
	.wallets {
		transition: all 0.4s linear;
		backdrop-filter: blur(30px) grayscale(0.5);
		position: fixed;
		display: flex;
		width: 100%;
		height: 100vh;
		justify-content: center;
		align-items: center;
		z-index: 10001;
	}

	.wallets-container {
		display: flex;
		flex-direction: column;
		width: 300px;
		overflow: hidden;
		border-radius: 10px;
	}

	.top {
		display: flex;
		height: 50px;
		justify-content: space-between;
		align-items: center;
		background-color: #2f83ff;
		padding-right: 5%;
		padding-left: 7%;
		padding-top: 2%;
		padding-bottom: 2%;
	}

	.name {
		font-size: 1rem;
	}

	.close {
		position: right;
		display: flex;
		height: 100%;
		justify-content: center;
		align-items: center;
		text-align: center;
		cursor: pointer;
	}

	.close img:hover {
		filter: drop-shadow(0px 0px 10px #c14179);
	}

	.item {
		display: flex;
		height: 40px;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		padding-right: 5%;
		padding-left: 7%;
		padding-top: 3%;
		padding-bottom: 3%;
		background-color: #3f4348;
		cursor: pointer;
	}

	.item:hover {
		background-color: rgb(52, 55, 59);
	}

	.item-name {
		color: white;
	}

	.hide {
		display: none;
	}
</style>
