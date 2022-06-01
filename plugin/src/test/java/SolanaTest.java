import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AccountInfoNotFoundException;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
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
            System.out.println(accountInfo.getId());
            System.out.println("");
        }
    }

    @Test
    public void getRegionByIDTest () throws AddressFormatException, ApiRequestException, IOException, AccountInfoNotFoundException {
        SolanaRPC rpc = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        PublicKey pk = new PublicKey("u35VEZ9gPkPg1VAp3YAxPejRhKKu5q8FJagEc7vUs6Y");


        RegionAccountInfo accountInfo = rpc.getAccountInfoByRegionID(pk, 4);
        System.out.println(accountInfo.getData());

        assertNotEquals(accountInfo, null);
        assertEquals(accountInfo.getId(), 4);

        ShortBuffer buf = ByteBuffer.wrap(accountInfo.getPayload()).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        short[] region = new short[buf.limit()];
        assertEquals(region.length, 16384);
    }
}
