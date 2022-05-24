package com.crypteam;

import com.crypteam.rcon.RConClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        RConClient client = RConClient.getInstance();
        APIResponseBuilder<Region> responseBuilder = new APIResponseBuilder<>();
        if (client == null)
            return responseBuilder.makeFailed("NullRconClient").build();

        try {
            Region region = new Region(client.readArea(id));
            return responseBuilder.setPayload(region).makeSuccess().build();
        } catch (Exception e) {
            return responseBuilder.makeFailed("Some error here...").build();
        }
    }
}
