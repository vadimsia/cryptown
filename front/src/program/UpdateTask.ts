import type { Program } from "./program";
import type { ProgramAccount } from "./ProgramAccount";

export class UpdateTask {
    private program: Program
    private account: ProgramAccount
    private offset: number
    private data: Buffer

    constructor (program: Program, account: ProgramAccount, offset: number, data: Buffer) {
        this.program = program
        this.account = account
        this.offset = offset
        this.data = data
    }

    public async execute () : Promise<string> {
        return await this.program.updateChunk(this.account, this.offset, this.data)
    }
}