package com.crypteam.rcon.commands;


import com.crypteam.WorldChange;

import java.io.*;

public class ReadAreaCommand implements RconCommand {

    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);

        int area_id = dis.readInt();

        for (int part :WorldChange.getRegion(area_id))
            dos.writeInt(part);

        dos.flush();
    }
}
