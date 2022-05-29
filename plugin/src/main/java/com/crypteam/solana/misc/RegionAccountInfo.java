package com.crypteam.solana.misc;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RegionAccountInfo extends AccountInfo {
    int id;
    PublicKey daddy;
    PublicKey owner;
    byte[] payload;


    public RegionAccountInfo(PublicKey publicKey, byte[] data) {
        super(publicKey, data);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.id = buffer.asIntBuffer().get(0);
        this.daddy = new PublicKey(Arrays.copyOfRange(data, 4, 36));
        this.owner = new PublicKey(Arrays.copyOfRange(data, 36, 68));
        this.payload = Arrays.copyOfRange(data, 68, data.length);
    }

    public RegionAccountInfo(AccountInfo accountInfo) {
        super(accountInfo.publicKey, accountInfo.data);

        ByteBuffer buffer = ByteBuffer.wrap(data);

        this.id = buffer.asIntBuffer().get(0);
        this.daddy = new PublicKey(Arrays.copyOfRange(data, 4, 36));
        this.owner = new PublicKey(Arrays.copyOfRange(data, 36, 68));
        this.payload = Arrays.copyOfRange(data, 68, data.length);
    }

    public PublicKey getDaddy() {
        return daddy;
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
