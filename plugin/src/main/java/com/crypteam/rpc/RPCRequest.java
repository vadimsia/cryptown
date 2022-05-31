package com.crypteam.rpc;

import java.io.Serializable;

public class RPCRequest implements Serializable {
    public RPCCommand command;

    public RPCRequest (RPCCommand command) {
        this.command = command;
    }
}
