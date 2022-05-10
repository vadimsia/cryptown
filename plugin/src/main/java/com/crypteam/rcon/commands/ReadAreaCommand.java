package com.crypteam.rcon.commands;


import com.crypteam.Section;

import java.io.*;

public class ReadAreaCommand implements RconCommand {

    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);

        int area_id = dis.readInt();
        Section sec = new Section(area_id);
        for (int part :sec.getRegion())
            dos.writeInt(part);

        dos.flush();
    }
}
