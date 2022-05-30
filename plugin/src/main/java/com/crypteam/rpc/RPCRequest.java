package com.crypteam.rpc;

import java.io.Serializable;

public class RPCRequest implements Serializable {
    public RPCCommand command;
    public byte[] payload;

    public RPCRequest (RPCCommand command, byte[] payload) {
        this.command = command;
        this.payload = payload;
    }
}
