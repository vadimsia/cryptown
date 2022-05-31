package com.crypteam.rpc;

import redis.clients.jedis.JedisPubSub;


public class RPCSubscriber extends JedisPubSub {

    public void onMessage(String channel, String message) {

    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener started!");
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener stopped!");
    }
}