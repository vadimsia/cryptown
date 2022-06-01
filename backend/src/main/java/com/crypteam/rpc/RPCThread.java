package com.crypteam.rpc;

import redis.clients.jedis.Jedis;

public class RPCThread extends Thread {
    Jedis jedis;
    RPCSubscriber listener;

    public RPCThread (Jedis jedis, RPCSubscriber listener) {
        this.jedis = jedis;
        this.listener = listener;
    }

    @Override
    public void run() {
        jedis.subscribe(listener, "rpc");
        System.out.println("RPC is shutting down...");
    }
}