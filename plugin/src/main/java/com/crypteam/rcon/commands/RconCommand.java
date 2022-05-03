package com.crypteam.rcon.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RconCommand {
    void execute(InputStream is, OutputStream os) throws IOException;
}
