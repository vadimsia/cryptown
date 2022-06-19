package com.crypteam;

import com.crypteam.crypto.TweetNaclFast;
import com.crypteam.rpc.*;
import com.crypteam.rpc.RPCSubscriber;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class Tests {


    @Test
    void byteCastTest () {
        ByteBuffer buf = ByteBuffer.wrap(new byte[] {1,2,3,4});

        short[] data = new short[buf.limit() / 2];
        buf.asShortBuffer().get(data);

        assertArrayEquals(new byte[] {1,2,3,4}, Utils.short2byte(data));
    }

    @Test
    void rpcTest2() throws IOException, InterruptedException {
        JedisPool pool = new JedisPool("localhost", 6379);
        ReadDataRequest request = new ReadDataRequest(1);
        ReadDataResponse fake_response = new ReadDataResponse(request, new short[] {1,2,3});
        try {
            new RPCPublisher(pool.getResource());
            new RPCSubscriber(pool.getResource());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("lol");
        RPCPublisher.publish(Serializer.serialize(fake_response));
        RPCRequest response = RPCSubscriber.waitMessage(request, RPCCommand.READ_DATA_RESPONSE);

        assertNotEquals(response, null);
    }

    @Test
    void signatureTest() {
        byte[] key = Base64.decodeBase64("tRNT3tbDvvux8w6TwATGm8wqPxD0wkCSWy27WJ3kiWc=");
        byte[] signature = Base64.decodeBase64("VIMffpLOsZtEAT8UsJ8s+NW1+DMTqYbY0DM+dvdsaTYTP1/cAzdD2Ip7bex0dHXfI3oe/zEfslZEwHoyhNX9Ag==");
        String msg = "Hello world!";

        TweetNaclFast.Signature sign = new TweetNaclFast.Signature(key, key);

        System.out.println(sign.detached_verify(msg.getBytes(), signature));
    }
}
