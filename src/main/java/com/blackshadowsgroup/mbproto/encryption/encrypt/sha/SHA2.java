package com.blackshadowsgroup.mbproto.encryption.encrypt.sha;

/**
 * Created by Behi198 on 10/2/2017.
 */

public class SHA2 {

    private static SHA2 instance;

    public static SHA2 getInstance(){
        if (instance == null)
            instance = new SHA2();
        return instance;
    }

    public byte[] hash(String string) {
        return SHA.getInstance().sha128(string);
    }
    public static byte[] hash256(String string) {
        return SHA.getInstance().sha256(string);
    }

    public byte[] hash256(byte[] bytes) {
        return SHA.getInstance().sha256(bytes);
    }

    public byte[] hash(byte[] bytes) {
        return SHA.getInstance().sha128(bytes);
    }

}

