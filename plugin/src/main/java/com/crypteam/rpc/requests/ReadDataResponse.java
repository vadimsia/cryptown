package com.crypteam.rpc.requests;

import com.crypteam.rpc.RPCCommand;
import com.crypteam.rpc.RPCRequest;

public class ReadDataResponse extends RPCRequest {
    public short[] payload;

    public ReadDataResponse(ReadDataRequest request, short[] payload) {
        super(RPCCommand.READ_DATA_RESPONSE);
        this.payload = payload;
        this.uuid = request.uuid;
    }
}
