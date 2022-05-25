package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() throws IOException {
		new RConClient();

		byte[] area = RConClient.getInstance().readArea(0);
		System.out.println("Area 0 length: " + area.length);

		area = RConClient.getInstance().readArea(35);
		System.out.println("Area 35 length: " + area.length);
	}

}
