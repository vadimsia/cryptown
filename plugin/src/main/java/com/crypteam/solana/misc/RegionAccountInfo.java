package com.crypteam.solana.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class RegionAccountInfo extends AccountInfo {
    int id;
    PublicKey owner;
    byte[] payload;


    public RegionAccountInfo(PublicKey publicKey, byte[] data) {
        super(publicKey, data);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.id = buffer.asIntBuffer().get(0);
        this.owner = new PublicKey(Arrays.copyOfRange(data, 4, 36));
        this.payload = Arrays.copyOfRange(data, 36, data.length);
    }

    public RegionAccountInfo(AccountInfo accountInfo) {
        super(accountInfo.publicKey, accountInfo.data);

        ByteBuffer buffer = ByteBuffer.wrap(data);

        this.id = buffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(0);
        this.owner = new PublicKey(Arrays.copyOfRange(data, 4, 36));
        this.payload = Arrays.copyOfRange(data, 36, data.length);
    }

    public PublicKey getOwner() {
        return owner;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getId() {
        return id;
    }
}
