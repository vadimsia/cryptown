package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.pando.crypto.nacl.Crypto;

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
		byte[] key = new byte[] {68, 66, 113, 116, 82, 122, 82, 84, 121, 70, 49, 98, 106, 121, 101, 71, 76, 89, 121, 66, 69, 52, 72, 75, 80, 53, 113, 65, 81, 77, 90, 51, 50, 66, 68, 122, 68, 101, 65, 54, 117, 81, 52, 110};

		PublicKey pk = Crypto.signingPublicKey(key);
		byte[] sign = Base64.decodeBase64("VIMffpLOsZtEAT8UsJ8s+NW1+DMTqYbY0DM+dvdsaTYTP1/cAzdD2Ip7bex0dHXfI3oe/zEfslZEwHoyhNX9Ag==");
		String msg = "Hello world!";

		System.out.println(Crypto.signVerify(pk, msg.getBytes(), sign));
	}
}
