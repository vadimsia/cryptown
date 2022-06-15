<script lang="ts">
	import { PhantomWallet } from '../wallets/PhantomWallet';
	import type { IWalletController } from '../wallets/IWalletController';

	import { onMount } from 'svelte';
	import { Buffer } from 'buffer'
    import { APIController } from '../api/APIController';

	let controller: IWalletController;

	onMount (async () => {
		window.Buffer = Buffer;

        await connectWallet();
	})

	// Подключение кошелька
	async function connectWallet() {
		controller = new PhantomWallet();
		await controller.connect();

        const queryString = window.location.search;

        const urlParams = new URLSearchParams(queryString);
        let uuid = urlParams.get("uuid")
        let nick = urlParams.get("nick")
        
        if (!uuid)
            throw 'undefined uuid!'

        let result = await controller.wallet.signMessage(uuid)
		let signature = result.signature.toString('base64')
        let pk = controller.wallet.publicKey.toBytes().toString('base64')

        console.log(await APIController.makeLogin(uuid, pk, signature))
	}



</script>

<div>
    Loginning...
</div>
