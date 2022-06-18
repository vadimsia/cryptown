package com.crypteam.rpc;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RPCJedisPool {
    private static JedisPool pool;

    public RPCJedisPool () {
        pool = new JedisPool("redis",6379);
    }

    public static Jedis getResource () {
        return  pool.getResource();
    }
}
