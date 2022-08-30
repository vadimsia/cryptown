package com.crypteam.rpc;

import java.io.*;
import java.util.Base64;

public class Serializer {
    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;

        out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.flush();
        byte[] yourBytes = bos.toByteArray();

        bos.close();

        return Base64.getEncoder().encodeToString(yourBytes);
    }

    public static Object deserialize(String b64string) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(b64string));
        ObjectInput in;

        in = new ObjectInputStream(bis);
        Object o = in.readObject();

        in.close();

        return o;
    }
}
