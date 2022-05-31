package com.crypteam.rpc;

import com.crypteam.rpc.exceptions.RPCWaitTimeout;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;


class Listener extends JedisPubSub {
    private RPCRequest request;
    private RPCRequest result;

    private RPCCommand waitCommand;

    public Listener (RPCRequest request, RPCCommand waitCommand) {
        this.request = request;
        this.waitCommand = waitCommand;
    }

    public void onMessage(String channel, String message) {
        System.out.println("Message here...");
        RPCRequest incomingRequest;
        try {
            incomingRequest = (RPCRequest) Serializer.deserialize(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (incomingRequest.uuid.compareTo(request.uuid) == 0 && incomingRequest.command == waitCommand) {
            result = request;
            unsubscribe();
            System.out.println("done");
        }
    }

    public RPCRequest getResult() {
        return result;
    }
}

class TimeoutThread extends Thread {
    private Listener listener;

    public TimeoutThread (Listener listener) {
        this.listener = listener;
    }

    @Override
    public void run () {
        try {
            Thread.sleep(1000 * 15);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
            return;
        }

        if (listener.isSubscribed())
            listener.unsubscribe();
    }
}



public class RPCWaitMessage {
    Jedis jedis;
    Listener listener;

    public RPCWaitMessage (Jedis jedis, RPCRequest request, RPCCommand waitCommand) {
        this.listener = new Listener(request, waitCommand);
        this.jedis = jedis;
    }

    public RPCRequest waitMessage () throws RPCWaitTimeout {
        TimeoutThread thread = new TimeoutThread(listener);
        thread.start();


        jedis.subscribe(listener, "rpc");
        thread.interrupt();

        if (listener.getResult() == null)
            throw new RPCWaitTimeout();

        return  listener.getResult();
    }
}
