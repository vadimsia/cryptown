package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.pando.crypto.nacl.Crypto;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
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
		int[] key = new int[] {181, 19, 83, 222, 214, 195, 190, 251, 177, 243, 14, 147, 192, 4, 198, 155, 204, 42, 63, 16, 244, 194, 64, 146, 91, 45, 187, 88, 157, 228, 137, 103};

		PublicKey pk = Crypto.signingPublicKey("DBqtRzRTyF1bjyeGLYyBE4HKP5qAQMZ32BDzDeA6uQ4n");
		byte[] sign = Base64.decodeBase64("VIMffpLOsZtEAT8UsJ8s+NW1+DMTqYbY0DM+dvdsaTYTP1/cAzdD2Ip7bex0dHXfI3oe/zEfslZEwHoyhNX9Ag==");
		String msg = "Hello world!";

		System.out.println(Crypto.signVerify(pk, msg.getBytes(), sign));
	}
}
