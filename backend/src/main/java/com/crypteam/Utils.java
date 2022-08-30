package com.crypteam;

public class Utils {
    public static byte[] short2byte (short[] arr) {
        byte[] result = new byte[arr.length * 2];
        for (int i = 0; i < arr.length; i++) {
            result[i * 2] = (byte) (arr[i] >> 8);
            result[i * 2 + 1] = (byte) arr[i];
        }

        return result;
    }
}
