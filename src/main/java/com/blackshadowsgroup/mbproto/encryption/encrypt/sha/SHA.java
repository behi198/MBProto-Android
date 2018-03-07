package com.blackshadowsgroup.mbproto.encryption.encrypt.sha;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Behi198 on 6/11/2017.
 */

public class SHA {

    private static SHA instance;

    public static SHA getInstance(){
        if (instance == null)
            instance = new SHA();
        return instance;
    }

    public byte[] sha128(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(data.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            return Arrays.copyOfRange(digest, 0, 16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    byte[] sha256(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(data.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            return md.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] sha128(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(data); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            return Arrays.copyOfRange(digest, 0, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] sha256(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(bytes); // Change this to "UTF-16" if needed
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
