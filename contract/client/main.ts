import {
    PublicKey,
    Connection
} from '@solana/web3.js'

import {
    getPayer,
    getRpcUrl
} from './utils'

import {
    Program
} from './program'


(async () => {
    const PROGRAM_ID = new PublicKey('9sbRLyisEEu3ybKwonrfyNxQNU2NZzxApTtYJhfBmXfK')

    let connection = new Connection(await getRpcUrl(), 'confirmed')
    let program = new Program(PROGRAM_ID, await getPayer(), connection)



    let token = new PublicKey('2vi52iuTAK3j9BcZdNHRb8B5g2QfAqLrXdq7q3CuJ1xn')
    let mint = new PublicKey('s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV')

    let accounts = await program.getOrCreateAccounts()
    //await program.initAccounts([accounts[0].pubkey])
    //await program.updateTokens([accounts[0].pubkey], [mint])
    let data = []
    for (let i = 0; i < 8; i++) 
        data.push(i)

    try {
        await program.updateChunk(accounts[0].pubkey, token, data)
    } catch (e) {
        console.log(e)
    }


    let result = await connection.getAccountInfo(accounts[0].pubkey)

    // @ts-ignore
    console.log(Uint8Array.from(result.data))
})()
