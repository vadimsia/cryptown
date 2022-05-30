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
		byte[] key = new byte[] {-75, 19, 83, -34, -42, -61, -66, -5, -79, -13, 14, -109, -64, 4, -58, -101, -52, 42, 63, 16, -12, -62, 64, -110, 91, 45, -69, 88, -99, -28, -119, 103};

		TweetNaclFast.Signature sign = new TweetNaclFast.Signature(key, key);
		byte[] signature = Base64.decodeBase64("VIMffpLOsZtEAT8UsJ8s+NW1+DMTqYbY0DM+dvdsaTYTP1/cAzdD2Ip7bex0dHXfI3oe/zEfslZEwHoyhNX9Ag==");
		String msg = "Hello world!";

		System.out.println(sign.detached_verify(msg.getBytes(), signature));
	}
}
