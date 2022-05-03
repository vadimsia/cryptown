package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;


@SpringBootApplication
public class DemoApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);

	public static ApplicationContext ctx;


	public static void main(String[] args) throws IOException {
		ctx = SpringApplication.run(DemoApplication.class, args);


		new RConClient();
	}

}
