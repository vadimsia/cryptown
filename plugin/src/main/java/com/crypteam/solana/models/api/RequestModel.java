package com.crypteam.solana.models.api;

import java.util.List;
import java.util.UUID;

public class RequestModel {
    public String id = UUID.randomUUID().toString();
    public String jsonrpc = "2.0";
    public String method;

    public List<String> params;
}
