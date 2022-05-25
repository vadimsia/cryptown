package com.crypteam.rcon;

import com.crypteam.rcon.commands.ReadAreaCommand;
import com.crypteam.rcon.commands.WriteAreaCommand;
import org.bukkit.Bukkit;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class RConServer implements Runnable {
    enum RconCommand {
        READ_AREA,
        WRITE_AREA
    }

    @Override
    public void run() {
        ServerSocket server;

        try {
            server = new ServerSocket(4004);

            Bukkit.getLogger().info("RCON Server started at :4004");

            while (!Thread.currentThread().isInterrupted()) {
                Socket client = server.accept();

                DataInputStream is = new DataInputStream(client.getInputStream());

                try {
                    switch (RconCommand.values()[is.readInt()]) {
                        case READ_AREA -> new ReadAreaCommand().execute(client.getInputStream(), client.getOutputStream());
                        case WRITE_AREA -> new WriteAreaCommand().execute(client.getInputStream(), client.getOutputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
