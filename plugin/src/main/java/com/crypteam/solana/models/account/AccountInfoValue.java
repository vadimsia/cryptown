package com.crypteam.solana.models.account;

import java.util.List;

public class AccountInfoValue {
    public List<String> data;
    public boolean executable;
    public long lamports;
    public String owner;
    public int rentEpoch;
}

