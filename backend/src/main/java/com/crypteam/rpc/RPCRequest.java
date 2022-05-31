package com.crypteam.rpc;

import java.io.Serializable;
import java.util.UUID;

public class RPCRequest implements Serializable {
    public RPCCommand command;
    public UUID uuid;

    public RPCRequest (RPCCommand command) {
        this.command = command;
        this.uuid = new UUID(10, 10);
    }
}
