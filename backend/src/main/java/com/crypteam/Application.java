package com.crypteam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;


@SpringBootApplication
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static ApplicationContext ctx;


	public static void main(String[] args) throws IOException {
		ctx = SpringApplication.run(Application.class, args);

		//new RConClient();
	}

}
