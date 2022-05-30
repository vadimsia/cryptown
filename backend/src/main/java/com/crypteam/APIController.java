package com.crypteam;

import com.crypteam.crypto.TweetNaclFast;
import com.crypteam.rcon.RConClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class APIResponse<T> {
    public boolean success = true;
    public String message = "";
    public T data;
}

class APIResponseBuilder<T> {
    private APIResponse<T> apiResponse = new APIResponse<>();
    public APIResponseBuilder<T> setPayload (T data) {
        apiResponse.data = data;
        return this;
    }

    public APIResponseBuilder<T> makeSuccess () {
        apiResponse.success = true;
        apiResponse.message = "";
        return this;
    }

    public APIResponseBuilder<T> makeFailed (String message) {
        apiResponse.success = false;
        apiResponse.message = message;
        apiResponse.data = null;
        return this;
    }

    public APIResponse<T> build () {
        return apiResponse;
    }
}

class Region {
    public byte[] region_raw;

    Region (byte[] data) {
        this.region_raw = data;
    }
}
@RestController
public class APIController {
    @GetMapping("/api/get_region/{id}")
    public APIResponse<Region> getRegion (@PathVariable int id) {
        APIResponseBuilder<Region> responseBuilder = new APIResponseBuilder<>();
        RConClient client;

        try {
            client = new RConClient();
        } catch (Exception e) {
            return responseBuilder.makeFailed("Cant connect to minecraft server...").build();
        }

        try {
            Region region = new Region(client.readArea(id));
            return responseBuilder.setPayload(region).makeSuccess().build();
        } catch (Exception e) {
            return responseBuilder.makeFailed("Some error here...").build();
        }
    }

    @GetMapping("/api/login")
    public APIResponse<Boolean> makeLogin (@RequestParam String uuid, @RequestParam String pk, @RequestParam String sig) {
        byte[] key = Base64.decodeBase64(pk);
        byte[] signature = Base64.decodeBase64(sig.replaceAll(" ", "+"));

        TweetNaclFast.Signature sign = new TweetNaclFast.Signature(key, key);
        APIResponseBuilder<Boolean> responseBuilder = new APIResponseBuilder<>();

        if (!sign.detached_verify(uuid.getBytes(), signature)) {
            return responseBuilder.makeFailed("Signature verification failed :/").build();
        }

        return responseBuilder.setPayload(true).makeSuccess().build();
    }
}
