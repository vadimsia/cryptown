package com.crypteam.solana.misc;


public class AccountInfo {
    protected PublicKey publicKey;
    protected byte[] data;

    public AccountInfo(PublicKey publicKey, byte[] data) {
        this.publicKey = publicKey;
        this.data = data;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
