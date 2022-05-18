package com.crypteam.rcon.commands;

import com.crypteam.Section;
import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.misc.AccountInfo;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class WriteAreaCommand implements RconCommand {
    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        String areaPK = dis.readAllBytes().toString();
        int areaID = Integer.valueOf(areaPK.substring(8, 11));

        SolanaRPC solanaRPC = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        AccountInfo accountInfo;

        try {
            accountInfo = solanaRPC.getAccountInfo(new PublicKey(areaPK));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RegionAccountInfo regionAccount = new RegionAccountInfo(accountInfo);

        IntBuffer intBuf = ByteBuffer.wrap(regionAccount.getPayload()).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] area_int = new int[intBuf.remaining()];
        Section sec = new Section(areaID);
        sec.setRegion(area_int);
    }
}
