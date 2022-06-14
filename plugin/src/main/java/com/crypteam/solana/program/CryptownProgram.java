package com.crypteam.solana.program;

import com.crypteam.solana.SolanaProgramProperties;
import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AccountInfoNotFoundException;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import com.crypteam.solana.misc.TokenAccountInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CryptownProgram {
    PublicKey programID;
    SolanaRPC rpc;

    public CryptownProgram () {
        this.programID = SolanaProgramProperties.PROGRAM_ID;
        this.rpc = new SolanaRPC();
    }

    public CryptownProgram (PublicKey programID, String rpcEndpoint) {
        this.programID = programID;
        this.rpc = new SolanaRPC(rpcEndpoint);
    }

    public List<RegionAccountInfo> getRegions () throws AddressFormatException, ApiRequestException, IOException {
        List<AccountInfo> accounts = this.rpc.getProgramAccounts(programID);
        List<RegionAccountInfo> regions = new ArrayList<>();

        for (AccountInfo account : accounts)
            regions.add(new RegionAccountInfo(account));

        return regions;
    }

    public RegionAccountInfo getRegionByID(int id) throws AddressFormatException, ApiRequestException, IOException, AccountInfoNotFoundException {
        for (RegionAccountInfo account : this.getRegions())
            if (account.getId() == id) return account;


        throw new AccountInfoNotFoundException();
    }

    public List<RegionAccountInfo> getRegionsByOwner(PublicKey owner) throws AddressFormatException, ApiRequestException, IOException {
        List<RegionAccountInfo> regions = this.getRegions();
        List<TokenAccountInfo> tokens = this.rpc.getTokenAccountsByOwner(owner);

        return regions.stream().filter(region -> {
            for (TokenAccountInfo token : tokens)
                if (region.getOwner().toString().compareTo(token.getMint().toString()) == 0)
                    return true;

            return false;
        }).collect(Collectors.toList());
    }
}
