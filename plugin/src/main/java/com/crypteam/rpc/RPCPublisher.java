package com.crypteam.rpc;

import redis.clients.jedis.Jedis;

public class RPCPublisher {
    private static Jedis instance;

    public RPCPublisher (Jedis jedis) {
        instance = jedis;
    }

    public Jedis getInstance () {
        return instance;
    }
}
