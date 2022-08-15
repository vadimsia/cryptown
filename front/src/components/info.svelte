<script lang="ts">
	let roadmap = [
		{ id: 1, state: false, name: 'Mint' },
		{ id: 2, state: false, name: 'Visualization' },
		{ id: 3, state: false, name: 'Marketplace' }
	];
</script>

<div class="info">
	<div class="about">
		<span class="main title">About</span>
		Cryp<span class="town">Town</span> - is a project that connects Minecraft and the Solana network.
		It allows you to immortalize your building in the network of Solana.
	</div>
	<div class="manual">
		<span class="main title">Manual</span>
		<span class="title">1. Mint</span>
		<span class="description">
			To begin with, to take part in the project, you need to connect a wallet and mint your region
			in the NFT format. There are 3 types of regions: <span class="region16">16x16x64</span>,
			<span class="region38">38x38x64</span>
			and <span class="region76">76x76x64</span>. Each region includes a unique building. You will
			randomly receive one of three types of region. The minting price will be 1 Sol.
		</span>
		<span class="title">2. Initialization and Update</span>
		<span class="description init">
			<span>
				After minting, you need to initialize your region. The initialization process allocates
				memory on the Solana network to store your region. The price of memory in the Solana network
				proportionally depends on its volume. Correspondence of the initialization price from the
				size of the region:
			</span>
			<span>
				<span class="region16">16x16x64</span> - 0.23 Sol
			</span>
			<span>
				<span class="region38">38x38x64</span> - 1.29 Sol
			</span>
			<span>
				<span class="region76">76x76x64</span> - 5.12 Sol
			</span>
			<span>
				After initialization, access to updating the region data. This operation allows you to put
				all the contents of your region into the Solana network.
			</span>
		</span>
		<span class="title">3. Game</span>
		<span class="description init">
			<span>
				After initialization, you can enter the game to see or change your region. Server IP - <span
					class="server">minecraft.cryptown.one</span
				>. The following commands are available on the server:
			</span>
			<span class="command">
				<span class="server">/login</span> - allows you to access your regions after authorization of
				the wallet on our website.
			</span>
			<span class="command">
				<span class="server">/goto regionId*</span> - teleports you to the specified region. The player
				is allowed to teleport to any region on the server.
			</span>
			<span class="command">
				<span class="server">/refresh</span> - downloads the selected region from the Solana network
				to the server. To use this command, you must be located on the territory of the selected region
				and be its owner.
			</span>
			<span>
				<span class="last">regionId - the id of the region indicated in the name of the NFT.</span>
			</span>
		</span>
	</div>
	<span class="main title">Roadmap</span>
	<div class="roadmap">
		{#each roadmap as item}
			<div class={'road-item ' + (item.state ? 'able' : 'dis')}>
				<div class={'id ' + (item.state ? 'able' : 'dis')}>{item.id}</div>
				<div class="name">{item.name}</div>
			</div>
		{/each}
	</div>
</div>

<style>
	.info {
		min-width: 760px;
		flex-direction: column;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 1vw;
		background-color: #00000096;
		width: 100%;
		margin-top: 10px;
		margin-right: 10px;
		border-radius: 4px;
		color: #e9e9e9e8;
		line-height: 1.53;
	}

	.about {
		margin-bottom: 50px;
	}

	.region16 {
		border-radius: 3px;
		padding: 1.5px;
		background-color: #01ad03;
	}

	.region38 {
		border-radius: 3px;
		padding: 1.5px;
		background-color: rgb(173, 12, 1);
	}

	.region76 {
		border-radius: 3px;
		padding: 1.5px;
		background-color: #2f83ff;
	}

	.town {
		border-radius: 2px;
		padding: 2px;
		background-color: #2f83ff;
	}

	.manual {
		display: flex;
		flex-direction: column;
		margin-bottom: 50px;
	}

	.last {
		margin-bottom: 0;
	}

	.title {
		display: flex;
		justify-content: center;
		margin-bottom: 20px;
		/* color: #2f83ff; */
	}
	.description {
		margin-bottom: 20px;
	}

	.init {
		display: flex;
		flex-direction: column;
	}

	.server {
		background: #707070;
		border-radius: 3px;
		padding: 1.5px;
	}

	.command {
		margin-top: 5px;
		margin-bottom: 5px;
	}

	.main {
		font-size: 22px;
	}

	.able {
		border: 3px solid #01ad03;
	}

	.dis {
		border: 3px solid yellow;
	}

	.roadmap {
		display: flex;
		flex-direction: row;
	}

	.road-item {
		padding-left: 10px;
		padding-right: 10px;
		width: 200px;
		height: 50px;
		display: flex;
		align-items: center;
		flex-direction: row;
		border-radius: 20px;
		margin-left: 30px;
		margin-right: 30px;
	}

	.id {
		padding: 3px;
		display: flex;
		justify-content: center;
		align-items: center;
		width: 30px;
		border-radius: 50%;
	}

	.name {
		width: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
	}
</style>
