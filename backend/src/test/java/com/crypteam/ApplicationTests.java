package com.crypteam;

import com.crypteam.crypto.TweetNaclFast;
import com.crypteam.rcon.RConClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import java.security.PublicKey;

@SpringBootTest
class ApplicationTests {

	@Test
	void rconTest() throws IOException {
		byte[] area = new RConClient().readArea(1);
		System.out.println("Area 0 length: " + area.length);

		area = new RConClient().readArea(36);
		System.out.println("Area 35 length: " + area.length);
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
