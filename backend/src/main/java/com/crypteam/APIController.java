package com.crypteam;

import com.crypteam.crypto.TweetNaclFast;
import com.crypteam.rpc.RPCCommand;
import com.crypteam.rpc.RPCPublisher;
import com.crypteam.rpc.RPCSubscriber;
import com.crypteam.rpc.requests.AuthorizeRequest;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

        try {
            ReadDataRequest request = new ReadDataRequest(id);
            RPCPublisher.publish(request);

            ReadDataResponse response = (ReadDataResponse) RPCSubscriber.waitMessage(request, RPCCommand.READ_DATA_RESPONSE);

            Region region = new Region(Utils.short2byte(response.payload));
            return responseBuilder.setPayload(region).makeSuccess().build();
        } catch (Exception e) {
            e.printStackTrace();
            return responseBuilder.makeFailed("Some error here...").build();
        }
    }

    @GetMapping("/api/login")
    public APIResponse<Boolean> makeLogin (@RequestParam String uuid, @RequestParam String pk, @RequestParam String sig) {
        byte[] key = Base64.decodeBase64(pk.replaceAll(" ", "+"));
        byte[] signature = Base64.decodeBase64(sig.replaceAll(" ", "+"));

        TweetNaclFast.Signature sign = new TweetNaclFast.Signature(key, key);
        APIResponseBuilder<Boolean> responseBuilder = new APIResponseBuilder<>();

        if (!sign.detached_verify(uuid.getBytes(), signature)) {
            return responseBuilder.makeFailed("Signature verification failed :/").build();
        }

        try {
            RPCPublisher.publish(new AuthorizeRequest(key, uuid));
        } catch (IOException e) {
            return responseBuilder.makeFailed("Internal server error...").build();
        }
        return responseBuilder.setPayload(true).makeSuccess().build();
    }
}
