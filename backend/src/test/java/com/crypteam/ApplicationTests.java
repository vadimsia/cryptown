package com.crypteam;

import com.crypteam.crypto.TweetNaclFast;
import com.crypteam.rpc.*;
import com.crypteam.rpc.exceptions.RPCWaitTimeout;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTests {

	@Test
	void rpcTest() throws RPCWaitTimeout {
		JedisPool pool = new JedisPool("localhost", 6379);
		RPCRequest request = new ReadDataRequest(1);
		RPCRequest response = new RPCWaitMessage(pool.getResource(), request, RPCCommand.READ_DATA_RESPONSE).waitMessage();
		assertEquals(response, null);
	}

	@Test
	void rpcTest2() throws IOException, InterruptedException {
		JedisPool pool = new JedisPool("localhost", 6379);
		ReadDataRequest request = new ReadDataRequest(1);
		ReadDataResponse fake_response = new ReadDataResponse(request, new short[] {1,2,3});

		new RPCPublisher(pool.getResource());

		AtomicReference<RPCRequest> response = new AtomicReference<>();
		Thread thread = new Thread(() -> {
			try {
				response.set(new RPCWaitMessage(pool.getResource(), request, RPCCommand.READ_DATA_RESPONSE).waitMessage());
			} catch (RPCWaitTimeout e) {
				return;
			}
			System.out.println("response here");
		});

		thread.start();

		Thread.sleep(1000);

		RPCPublisher.publish(Serializer.serialize(fake_response));
		while (thread.isAlive()) {
			Thread.sleep(100);
		}

		assertNotEquals(response.get(), null);
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
