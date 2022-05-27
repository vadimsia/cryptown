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
    const PROGRAM_ID = new PublicKey('BoJibLNDthR9j5A4SqpzSGTdULiR8d43kEat3bbbufhq')

    let connection = new Connection(await getRpcUrl(), 'confirmed')
    let program = new Program(PROGRAM_ID, await getPayer(), connection)



    let token = new PublicKey('2vi52iuTAK3j9BcZdNHRb8B5g2QfAqLrXdq7q3CuJ1xn')
    let mint = new PublicKey('s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV')

    let accounts = await program.getOrCreateAccounts()
    // console.log(accounts[0].pubkey.toBase58())
    // await program.initAccounts(accounts)
    //await program.updateTokens([accounts[0].pubkey], [mint])
    // let data = []
    // for (let i = 0; i < 32; i++) 
    //     data.push(i)

    // try {
    //     await program.updateChunk(accounts[0].pubkey, token, 0, data)
    // } catch (e) {
    //     console.log(e)
    // }

    for (let account of accounts) {
        let result = await connection.getAccountInfo(account.pubkey)

        // @ts-ignore
        console.log(Uint8Array.from(result.data).slice(0, 4))
    }
})()
