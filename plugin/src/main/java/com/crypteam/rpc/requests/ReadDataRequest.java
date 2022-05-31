package com.crypteam.rpc.requests;

import com.crypteam.rpc.RPCCommand;
import com.crypteam.rpc.RPCRequest;

public class ReadDataRequest extends RPCRequest {
    public int area_id;

    public ReadDataRequest (int area_id) {
        super(RPCCommand.READ_DATA);
        this.area_id = area_id;
    }
}
