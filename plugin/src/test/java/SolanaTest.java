import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SolanaTest {
    @Test
    public void publicKeyTest () throws AddressFormatException {
        String key = "16UjcYNBG9GTK4uq2f7yYEbuifqCzoLMGS";
        PublicKey pk = new PublicKey(key);


        assertEquals(key, pk.toString());
    }

    @Test
    public void RPCTest () throws AddressFormatException, IOException {
        SolanaRPC rpc = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        PublicKey pk = new PublicKey("s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV");

        try {
            AccountInfo accountInfo = rpc.getAccountInfo(pk);
            assertEquals(accountInfo.getPublicKey().toString(), pk.toString());
        } catch (ApiRequestException e) {
            System.out.println(e);
        }

        pk = new PublicKey("52UjnFqhUyPvRPxhbvdSWcgzJ7oDikHeD9DFe9q7EHHL");

        try {
            AccountInfo accountInfo = rpc.getAccountInfo(pk);
            RegionAccountInfo regionAccount = new RegionAccountInfo(accountInfo);

            assertEquals(regionAccount.getOwner().toString(), "s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV");
            assertEquals(regionAccount.getDaddy().toString(), "HCMDYFaAWD3YuaBMLiftc5MzNKcLrPmjASRaciRdAAYU");
            assertArrayEquals(regionAccount.getPayload(), new byte[] { 1, 1, 1, 1, 1, 1, 1, 1 });
        } catch (ApiRequestException e) {
            System.out.println(e);
        }
    }
    @Test
    public void getProgramAccountsTest () throws AddressFormatException, ApiRequestException, IOException {
        SolanaRPC rpc = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        PublicKey pk = new PublicKey("AoNiQdgpqwE1PYc5R5gYqxWv9nQtr3xN3gTEdGb4tFeW");


        List<AccountInfo> accounts = rpc.getProgramAccounts(pk);
        for (AccountInfo account : accounts) {
            RegionAccountInfo accountInfo = new RegionAccountInfo(account);
            System.out.println(accountInfo.getPublicKey());
            System.out.println(accountInfo.getDaddy());
            System.out.println(accountInfo.getId());
            System.out.println("");
        }
    }

    @Test
    public void getRegionByIDTest () throws AddressFormatException, ApiRequestException, IOException {
        SolanaRPC rpc = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        PublicKey pk = new PublicKey("AoNiQdgpqwE1PYc5R5gYqxWv9nQtr3xN3gTEdGb4tFeW");


        RegionAccountInfo accountInfo = rpc.getAccountInfoByRegionID(pk, 0);
        assertNotEquals(accountInfo, null);
        accountInfo = rpc.getAccountInfoByRegionID(pk, 1);
        assertEquals(accountInfo, null);
    }
}
