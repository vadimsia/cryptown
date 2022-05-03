import {
    Keypair,
    PublicKey,
    Connection
} from '@solana/web3.js'

import {
    getPayer,
    getRpcUrl,
    createKeypairFromFile
} from './utils'

import {
    Program
} from './program'


(async () => {
    const PROGRAM_ID = new PublicKey('6E4t31yQUeSwj1ZDKxyPpBYWWfoS4SGftEd3yeKCg2mx')

    let connection = new Connection(await getRpcUrl(), 'confirmed')
    let program = new Program(PROGRAM_ID, await getPayer(), connection)


    let accounts = await program.getOrCreateAccounts()

    let token = Keypair.generate().publicKey
    console.log(token.toBase58())

    //await program.updateTokens([accounts[0].pubkey], [token])
    await program.updateChunk(accounts[0].pubkey, [1,2,3,4,5,6,7,8])
    console.log(Array.from(Buffer.from(accounts[0].account.data)).slice(64))
})()
