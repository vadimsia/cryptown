package com.crypteam.rcon;

import com.crypteam.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RConClient {
    private static RConClient instance = null;

    Logger LOGGER = LoggerFactory.getLogger(Application.class);

    Socket socket;

    enum RconCommand {
        READ_AREA,
        WRITE_AREA
    }

    public RConClient () throws IOException {
        if (instance != null) return;

        socket = new Socket();
        socket.connect(new InetSocketAddress(4004));

        instance = this;
    }

    public byte[] readArea (int id) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeInt(RconCommand.READ_AREA.ordinal());
        dos.writeInt(id);

        dos.flush();

        byte[] data = socket.getInputStream().readNBytes(16*16*2);

        return data;
    }

    public void refreshArea (int id) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeInt(RconCommand.WRITE_AREA.ordinal());
        dos.writeInt(id);

        dos.flush();
    }

    public static RConClient getInstance() {
        return instance;
    }
}
