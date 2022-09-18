<script lang="ts">
	import { toolbarItems } from '../store/store';
	import Wallets from '../components/wallets.svelte';
	import { onMount } from 'svelte';
	import { Buffer } from 'buffer';
	import Toolbar from '../components/toolbar.svelte';
	import Server from '../components/server.svelte';

	let loaded = false;

	onMount(() => {
		loaded = true;
		window.Buffer = Buffer;
	});
</script>

<Wallets />
<div class="main">
	<div class={loaded ? 'container-1 done' : 'container-1'}>
		<div class="preloader">
			<img alt="loader" src="/loader.svg" width="50px" height="50px" />
		</div>
	</div>
	<div class="container-2">
		<div class="content">
			<div class="toolbar-container">
				<Toolbar />
			</div>
			<div class="section">
				<div class="left">
					{#each $toolbarItems as item}
						{#if item.state}
							<svelte:component this={item.component} />
						{/if}
					{/each}
				</div>
				<div class="right">
					<div class="server">
						<Server />
					</div>
					<div class="social">
						<div class="first-level">
							<a href="" alt="" class="button twitter"
								><img src="/twitter.svg" alt="" width="15px" />Twitter</a
							>
							<a href="" alt="" class="button discord"
								><img src="/discord.svg" alt="" width="15px" />Discord</a
							>
						</div>
						<div class="second-level">
							<a href="" alt="" class="button telegram"
								><img src="/telegram.svg" alt="" width="15px" />Telegram</a
							>
						</div>
					</div>
					<div class="discord-box">
						<iframe
							title=""
							src="https://discord.com/widget?id=899514519028645928&theme=dark"
							width="100%"
							height="300"
							allowtransparency={true}
							frameborder="0"
							sandbox="allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts"
						/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<style>
	.main {
		display: flex;
		position: relative;
		width: 100%;
		height: 100%;
		min-height: 100vh;
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
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		min-width: 1500px;
		min-height: 100vh;
		background: no-repeat url('/background.png');
		background-size: 100% 100%;
		overflow: auto;
	}

	.content {
		display: flex;
		flex-direction: column;
		height: 100%;
		width: 85%;
		min-width: 1500px;
	}

	.toolbar-container {
		display: flex;
		justify-content: center;
		width: 100%;
	}

	.section {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
	}

	.left {
		display: flex;
		width: 80%;
		margin-bottom: 50px;
	}

	.right {
		display: flex;
		flex-direction: column;
		width: 20%;
	}

	.right div {
		margin-bottom: 10px;
	}

	.social {
		background-color: #00000096;
		display: flex;
		padding: 10px;
		justify-content: space-between;
		align-items: center;
		border-radius: 4px;
		flex-direction: column;
	}

	.first-level {
		display: flex;
		width: 100%;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}

	.second-level {
		display: flex;
		justify-content: center;
		width: 100%;
	}

	.button {
		text-decoration: none;
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0;
		cursor: pointer;
		border-radius: 4px;
		width: 140px;
		height: 35px;
		color: black;
	}

	.twitter {
		background-color: #00acee;
	}

	.discord {
		background-color: #5865f2;
	}

	.telegram {
		background-color: #229ed9;
	}

	.button img {
		margin-right: 5px;
	}
</style>
