package com.crypteam.rpc;

import com.crypteam.Section;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;

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
            ReadDataRequest typed_request = (ReadDataRequest) request;
            Section sec = new Section(typed_request.area_id);

            ReadDataResponse response = new ReadDataResponse(sec.getRegion());

            try {
                RPCPublisher.publish(Serializer.serialize(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener started!");
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener stopped!");
    }
}