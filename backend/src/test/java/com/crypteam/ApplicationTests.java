package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() throws IOException {
		byte[] area = new RConClient().readArea(1);
		System.out.println("Area 0 length: " + area.length);

		area = new RConClient().readArea(36);
		System.out.println("Area 35 length: " + area.length);
	}

}
