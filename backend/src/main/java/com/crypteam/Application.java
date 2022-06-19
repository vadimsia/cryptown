package com.crypteam;

import com.crypteam.rpc.RPCJedisPool;
import com.crypteam.rpc.RPCPublisher;
import com.crypteam.rpc.RPCSubscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Application {

	public static ApplicationContext ctx;


	public static void main(String[] args) {
		ctx = SpringApplication.run(Application.class, args);

		new RPCJedisPool();
		new RPCPublisher(RPCJedisPool.getResource());
		new RPCSubscriber(RPCJedisPool.getResource());
	}

}
