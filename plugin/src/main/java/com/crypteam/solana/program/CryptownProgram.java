package com.crypteam.solana.program;

import com.crypteam.solana.SolanaProgramProperties;
import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AccountInfoNotFoundException;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;

import java.io.IOException;
import java.util.List;

public class CryptownProgram {
    PublicKey programID;
    SolanaRPC rpc;

    public CryptownProgram () {
        this.programID = SolanaProgramProperties.PROGRAM_ID;
        this.rpc = new SolanaRPC(SolanaProgramProperties.RPC_ENDPOINT);
    }

    public CryptownProgram (PublicKey programID, String rpcEndpoint) {
        this.programID = programID;
        this.rpc = new SolanaRPC(rpcEndpoint);
    }

    public RegionAccountInfo getAccountInfoByRegionID (int id) throws AddressFormatException, ApiRequestException, IOException, AccountInfoNotFoundException {
        List<AccountInfo> accounts = this.rpc.getProgramAccounts(programID);

        for (AccountInfo account : accounts) {
            RegionAccountInfo accountInfo = new RegionAccountInfo(account);
            if (accountInfo.getId() == id) return accountInfo;
        }

        throw new AccountInfoNotFoundException();
    }
}
