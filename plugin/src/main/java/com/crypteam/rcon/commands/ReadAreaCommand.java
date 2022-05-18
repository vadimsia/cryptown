package com.crypteam.rcon.commands;


import com.crypteam.Section;

import java.io.*;

public class ReadAreaCommand implements RconCommand {

    @Override
    public void execute(InputStream is, OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);

        String areaPK = dis.readAllBytes().toString();
        int areaID = Integer.valueOf(areaPK.substring(8, 11));

        Section sec = new Section(areaID);
        for (int part :sec.getRegion())
            dos.writeInt(part);

        dos.flush();
    }
}
