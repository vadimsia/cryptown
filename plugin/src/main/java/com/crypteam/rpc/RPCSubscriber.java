package com.crypteam.rpc;

import com.crypteam.Section;
import redis.clients.jedis.JedisPubSub;

public class RPCSubscriber extends JedisPubSub {
    public void onMessage(String channel, String message) {
        RPCRequest request;
        try {
            request = (RPCRequest) Serializer.deserialize(message);
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
            return;
        }

        if (request.command == RPCCommand.READ_DATA) {
            Section sec = new Section(0);
            //RPCRequest response = new RPCRequest(RPCCommand.READ_DATA_RESPONSE,)
        }
        System.out.println(request.command);
        for (byte b : request.payload)
            System.out.println(b);
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener started!");
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener stopped!");
    }
}