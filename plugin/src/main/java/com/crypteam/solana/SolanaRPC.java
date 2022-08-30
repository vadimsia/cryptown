package com.crypteam.solana;

import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.TokenAccountInfo;
import com.crypteam.solana.models.account.AccountInfoModel;
import com.crypteam.solana.models.api.ErrorModel;
import com.crypteam.solana.models.api.RequestModel;
import com.crypteam.solana.models.programaccounts.ProgramAccount;
import com.crypteam.solana.models.programaccounts.ProgramAccountsModel;
import com.crypteam.solana.models.tokenaccounts.TokenAccountsModel;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SolanaRPC {

    private final String endpoint;

    public SolanaRPC(String endpoint) {
        this.endpoint = endpoint;
    }
    public SolanaRPC() { this.endpoint = SolanaProgramProperties.RPC_ENDPOINT; }

    private String apiRequest (String requestJson) throws IOException, ApiRequestException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(this.endpoint);

        request.setEntity(new StringEntity(requestJson));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(request);
        String body = EntityUtils.toString(response.getEntity());
        ErrorModel errorModel = new Gson().fromJson(body, ErrorModel.class);

        if (errorModel.error != null) throw new ApiRequestException(errorModel.error.message);

        return body;
    }


    public AccountInfo getAccountInfo (PublicKey accountKey) throws IOException, ApiRequestException {
        List<String> params = new ArrayList<>();
        params.add(accountKey.toString());
        params.add("%replace%");

        RequestModel requestModel = new RequestModel();
        requestModel.method = "getAccountInfo";
        requestModel.params = params;

        String requestJson = new Gson().toJson(requestModel);
        requestJson = requestJson.replace("\"%replace%\"", "{\"encoding\": \"base64\", \"commitment\": \"confirmed\"}");

        String response = apiRequest(requestJson);
        AccountInfoModel accountInfoModel = new Gson().fromJson(response, AccountInfoModel.class);

        if (accountInfoModel.result.value == null) throw new ApiRequestException("Empty value");

        String data = accountInfoModel.result.value.data.get(0);

        return new AccountInfo(accountKey, Base64.decodeBase64(data.getBytes(StandardCharsets.UTF_8)));
    }

    public List<AccountInfo> getProgramAccounts (PublicKey programKey) throws ApiRequestException, IOException, AddressFormatException {
        List<String> params = new ArrayList<>();
        params.add(programKey.toString());
        params.add("%replace%");

        RequestModel requestModel = new RequestModel();
        requestModel.method = "getProgramAccounts";
        requestModel.params = params;

        String requestJson = new Gson().toJson(requestModel);
        requestJson = requestJson.replace("\"%replace%\"", "{\"encoding\": \"base64\"}");

        String response = apiRequest(requestJson);
        ProgramAccountsModel programAccountsModel = new Gson().fromJson(response, ProgramAccountsModel.class);

        if (programAccountsModel.result == null) throw new ApiRequestException("Empty value");

        List<AccountInfo> result = new ArrayList<>();
        for (ProgramAccount accountInfo : programAccountsModel.result)
            result.add(new AccountInfo(new PublicKey(accountInfo.pubkey), Base64.decodeBase64(accountInfo.account.data.get(0).getBytes(StandardCharsets.UTF_8))));

        return result;
    }

    public List<TokenAccountInfo> getTokenAccountsByOwner (PublicKey owner) throws ApiRequestException, IOException, AddressFormatException {
        List<String> params = new ArrayList<>();
        params.add(owner.toString());
        params.add("%replace%");
        params.add("%replace2%");

        RequestModel requestModel = new RequestModel();
        requestModel.method = "getTokenAccountsByOwner";
        requestModel.params = params;

        String requestJson = new Gson().toJson(requestModel);
        requestJson = requestJson.replace("\"%replace%\"", "{\"programId\": \"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\"}");
        requestJson = requestJson.replace("\"%replace2%\"", "{\"encoding\": \"base64\"}");

        String response = apiRequest(requestJson);
        TokenAccountsModel tokenAccountsModel = new Gson().fromJson(response, TokenAccountsModel.class);

        if (tokenAccountsModel.result == null) throw new ApiRequestException("Empty value");

        List<TokenAccountInfo> result = new ArrayList<>();
        for (ProgramAccount account : tokenAccountsModel.result.value) {
            AccountInfo accountInfo = new AccountInfo(
                    new PublicKey(account.pubkey),
                    Base64.decodeBase64(account.account.data.get(0).getBytes(StandardCharsets.UTF_8))
            );

            result.add(new TokenAccountInfo(accountInfo));
        }


        return result.stream().filter(token -> token.getAmount() == 1).collect(Collectors.toList());
    }
}
