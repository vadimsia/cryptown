package com.crypteam.rpc;

import redis.clients.jedis.Jedis;

import java.io.IOException;

public class RPCPublisher {
    private static Jedis instance;

    public RPCPublisher (Jedis jedis) {
        instance = jedis;
    }
    public static void publish (String message) {
        instance.publish("rpc", message);
    }
    public static void publish (Object o) throws IOException {
        instance.publish("rpc", Serializer.serialize(o));
    }
}
