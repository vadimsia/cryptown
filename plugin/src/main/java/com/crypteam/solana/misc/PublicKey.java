package com.crypteam.solana.misc;

import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.crypto.Base58;

public class PublicKey {
    private final byte[] key;

    public PublicKey(String value) throws AddressFormatException {
        this.key = Base58.decode(value);
    }

    public PublicKey(byte[] key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return Base58.encode(key);
    }
}
