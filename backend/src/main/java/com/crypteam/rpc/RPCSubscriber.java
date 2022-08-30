package com.crypteam.rpc;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;


public class RPCSubscriber extends JedisPubSub {
    private static RPCSubscriber instance;
    private List<RPCRequest> requests = new ArrayList<>();

    public RPCSubscriber(Jedis jedis) {
        instance = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                jedis.subscribe(RPCSubscriber.instance, "rpc");
            }
        }).start();
    }
    public void onMessage(String channel, String message) {
        try {
            RPCRequest incomingRequest = (RPCRequest) Serializer.deserialize(message);
            System.out.println("Got message: " + incomingRequest.command);
            this.requests.add(incomingRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static RPCRequest waitMessage (RPCRequest request, RPCCommand waitCommand) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            for (RPCRequest incomingRequest : instance.requests) {
                if (incomingRequest.uuid.compareTo(request.uuid) == 0 && incomingRequest.command == waitCommand) {
                    instance.requests.remove(incomingRequest);
                    return incomingRequest;
                }
            }

            Thread.sleep(100);
        }

        return null;
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener started!");
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener stopped!");
    }
}