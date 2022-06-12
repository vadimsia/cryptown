package com.crypteam.rpc.requests;

import com.crypteam.rpc.RPCCommand;
import com.crypteam.rpc.RPCRequest;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.misc.PublicKey;

public class AuthorizeRequest extends RPCRequest {
    byte[] key;
    String uuid;

    public AuthorizeRequest(byte[] key, String uuid) throws AddressFormatException {
        super(RPCCommand.AUTHORIZE_USER);

        this.key = key;
        this.uuid = uuid;
    }

    public PublicKey getKey() {
        return new PublicKey(key);
    }

    public String getUuid() {
        return uuid;
    }
}
