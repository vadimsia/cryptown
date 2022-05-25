package com.crypteam.rcon.commands;

import com.crypteam.Section;
import com.crypteam.solana.SolanaProgramID;
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
import java.nio.ShortBuffer;

public class WriteAreaCommand implements RconCommand {
    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        int areaID = dis.readInt();

        SolanaRPC solanaRPC = new SolanaRPC("https://explorer-api.devnet.solana.com/");
        RegionAccountInfo accountInfo;

        try {
            accountInfo = solanaRPC.getAccountInfoByRegionID(SolanaProgramID.PROGRAM_ID, areaID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ShortBuffer shortBuf = ByteBuffer.wrap(accountInfo.getPayload()).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        Section sec = new Section(areaID);
        sec.setRegion(new short[shortBuf.remaining()]);
    }
}
