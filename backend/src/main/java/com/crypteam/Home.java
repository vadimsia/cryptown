package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Home {
    @GetMapping("/")
    public String home () {
        short x = 0;
        short y = 0;

        try {
            byte[] data = new byte[16*16*2];
            for (int i = 0; i < 16*16*2; i++)
                data[i] = 1;
            
            RConClient.getInstance().writeArea(x, y, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Hello world!!!";
    }
}
