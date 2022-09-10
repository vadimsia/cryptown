<script lang="ts">
</script>

<div class="server">
	<div class="content">
		<div class="name enable-name">CrypTown</div>
		<!-- <div class={server_value.state? "boop enable-param-boop": "boop disable-param-boop"}></div> -->
		<div class="params">
			<div class="online">
				<div class="param enable-param-boop">Minecraft version:</div>
				<div class="value">1.18.2</div>
			</div>
			<div class="ip">
				<div class="param enable-param-boop">Server IP:</div>
				<div class="value">minecraft.cryptown.one</div>
			</div>
		</div>
	</div>
</div>

<style>
	.content {
		margin-top: 10px;
		border-radius: 4px;
		padding-right: 20px;
		padding-left: 20px;
		padding-top: 20px;
		padding-bottom: 20px;
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
		align-items: center;
		background-color: #00000096;
	}

	.name {
		font-family: 'CALVIN';
		padding: 5px;
		border-radius: 7px;
		font-size: 1.7rem;
		border: 2px solid;
	}

	.online {
		justify-content: center;
		display: flex;
		flex-direction: row;
		font-size: 12px;
		margin-bottom: 10px;
	}

	.ip {
		white-space: nowrap;
		display: flex;
		flex-direction: row;
		font-size: 12px;
	}

	.params {
		margin-top: 20px;
	}

	.param {
		border-radius: 4px;
		padding: 3px;
		/* color: #e9e9e9e8; */
		margin-right: 5px;
	}

	.value {
		display: flex;
		justify-content: center;
		margin-top: 3px;
		color: #e9e9e9e8;
	}

	.enable-param-boop {
		background-color: rgb(1, 173, 3);
	}

	.enable-name {
		color: rgb(1, 173, 3);
	}
</style>
