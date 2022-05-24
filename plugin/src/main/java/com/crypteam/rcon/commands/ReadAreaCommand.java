package com.crypteam.rcon.commands;


import com.crypteam.Section;

import java.io.*;

public class ReadAreaCommand implements RconCommand {

    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);

        int areaID = dis.readInt();

        Section sec = new Section(areaID);
        for (short part :sec.getRegion())
            dos.writeShort(part);

        dos.flush();
    }
}
