package com.crypteam.solana.misc;

import java.nio.ByteBuffer;

public class RegionAccountInfo extends AccountInfo {
    int id;
    PublicKey daddy;
    PublicKey owner;
    byte[] data;


    public RegionAccountInfo(PublicKey publicKey, byte[] data) {
        super(publicKey, data);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.id
    }
}
