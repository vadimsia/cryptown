import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import org.junit.Test;

import java.io.IOException;

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

        pk = new PublicKey("9v4t7MNLyzXRYgqwLU6bzq8jf4haWfrSziVwzXHPRDcX");

        try {
            AccountInfo accountInfo = rpc.getAccountInfo(pk);
            RegionAccountInfo regionAccount = new RegionAccountInfo(accountInfo);
            System.out.println(regionAccount.getOwner().toString());
        } catch (ApiRequestException e) {
            System.out.println(e);
        }
    }
}
