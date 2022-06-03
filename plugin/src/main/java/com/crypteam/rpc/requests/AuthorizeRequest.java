package com.crypteam.rpc.requests;

import com.crypteam.rpc.RPCCommand;
import com.crypteam.rpc.RPCRequest;

public class AuthorizeRequest extends RPCRequest {
    byte[] key;
    String uuid;

    public AuthorizeRequest(byte[] key, String uuid) {
        super(RPCCommand.AUTHORIZE_USER);

        this.key = key;
        this.uuid = uuid;
    }
}
