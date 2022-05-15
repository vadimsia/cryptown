import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.exceptions.ApiRequestException;
import com.crypteam.solana.misc.PublicKey;
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
        try {
            rpc.getAccountInfo(new PublicKey("s5E5TCxx2DNjoENQ6wmRQNR2pVjNFC1cy1tWfaFLQNV"));
        } catch (ApiRequestException e) {
            System.out.println(e);
        }
    }
}
