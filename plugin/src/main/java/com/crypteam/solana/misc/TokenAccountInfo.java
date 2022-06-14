package com.crypteam.solana.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class TokenAccountInfo extends AccountInfo {
    PublicKey mint;
    PublicKey owner;
    long amount;

    public TokenAccountInfo(AccountInfo accountInfo) {
        super(accountInfo.publicKey, accountInfo.data);

        ByteBuffer buffer = ByteBuffer.wrap(data);

        this.amount = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(8);
        this.mint = new PublicKey(Arrays.copyOfRange(data, 0, 32));
        this.owner = new PublicKey(Arrays.copyOfRange(data, 32, 64));
    }

    public PublicKey getOwner() {
        return owner;
    }

    public PublicKey getMint() {
        return mint;
    }

    public long getAmount() {
        return amount;
    }
}
