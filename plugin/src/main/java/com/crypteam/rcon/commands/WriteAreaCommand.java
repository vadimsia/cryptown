package com.crypteam.rcon.commands;

import com.crypteam.WorldChange;

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

        int area_id = dis.readInt();
        byte[] area = dis.readAllBytes();

        IntBuffer intBuf = ByteBuffer.wrap(area).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] area_int = new int[intBuf.remaining()];

        WorldChange.setRegion(area_id, area_int);
    }
}
