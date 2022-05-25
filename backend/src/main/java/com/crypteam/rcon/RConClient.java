package com.crypteam.rcon;

import com.crypteam.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RConClient {

    Logger LOGGER = LoggerFactory.getLogger(Application.class);

    Socket socket;

    enum RconCommand {
        READ_AREA,
        WRITE_AREA
    }

    public RConClient () throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(4004));

    }

    public byte[] readArea (int id) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeInt(RconCommand.READ_AREA.ordinal());
        dos.writeInt(id);

        dos.flush();


        int length = dis.readInt();

        byte[] data = new byte[length * 2];
        dis.readFully(data);

        return data;
    }

    public void refreshArea (int id) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeInt(RconCommand.WRITE_AREA.ordinal());
        dos.writeInt(id);

        dos.flush();
    }

}
