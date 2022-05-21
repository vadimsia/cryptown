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
        this.daddy = new PublicKey(Arrays.copyOfRange(data, 0, 33));
        this.owner = new PublicKey(Arrays.copyOfRange(data, 33, 66));
        this.payload = Arrays.copyOfRange(data, 66, data.length);
    }

    public RegionAccountInfo(AccountInfo accountInfo) {
        super(accountInfo.publicKey, accountInfo.data);

        this.daddy = new PublicKey(Arrays.copyOfRange(data, 0, 32));
        this.owner = new PublicKey(Arrays.copyOfRange(data, 32, 64));
        this.payload = Arrays.copyOfRange(data, 64, data.length);
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
}
