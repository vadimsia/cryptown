<button on:click={connectWallet}>Authorize using Phantom</button>
{#if controller}
    <button on:click={updateChunk}>Update chunk</button>
{/if}


<script lang="ts">
    import { PhantomWallet } from "../wallets/PhantomWallet";
    import type { IWalletController } from "../wallets/IWalletController"
    import { Program } from "../program/program";

    import { PublicKey } from "@solana/web3.js"

    const PROGRAM_ID = new PublicKey("9sbRLyisEEu3ybKwonrfyNxQNU2NZzxApTtYJhfBmXfK")

    
    let controller: IWalletController
    

    async function connectWallet () {
        controller = new PhantomWallet()
        await controller.connect()
        
        console.log(controller.Wallet)
    }

    async function updateChunk () {
        let program = new Program(PROGRAM_ID, controller.Wallet)

        let token = new PublicKey("2vi52iuTAK3j9BcZdNHRb8B5g2QfAqLrXdq7q3CuJ1xn")
        // let accounts = await program.getAccounts()

        // console.log(accounts[0].pubkey.toBase58())

        program.updateChunk(new PublicKey("52UjnFqhUyPvRPxhbvdSWcgzJ7oDikHeD9DFe9q7EHHL"), token, [0, 1, 2, 3, 4, 5, 6, 7])
    }

    let variable = "test";

</script>