<script lang="ts">
	import { server } from '../store/store';

	let server_value: object;
	server.subscribe((value) => {
		server_value = value;
	});
</script>

<div class="server">
	<div class="content">
		<div class={server_value.state ? 'name enable-name' : 'name disable-name'}>CrypTown</div>
		<!-- <div class={server_value.state? "boop enable-param-boop": "boop disable-param-boop"}></div> -->
		<div class="params">
			<div class="online">
				<div class={server_value.state ? 'param enable-param-boop' : 'param disable-param-boop'}>
					Online:
				</div>
				<div class="value">{'' + server_value.online + '/' + server_value.maxOnline}</div>
			</div>
			<div class="ip">
				<div class={server_value.state ? 'param enable-param-boop' : 'param disable-param-boop'}>
					Server IP:
				</div>
				<div class="value">minecraft.cryptown.one</div>
			</div>
		</div>
	</div>
</div>

<style>
	.server {
	}

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

	.disable-param-boop {
		background-color: rgb(173, 12, 1);
	}

	.disable-name {
		color: rgb(173, 12, 1);
	}

	.enable-name {
		color: rgb(1, 173, 3);
	}
</style>
