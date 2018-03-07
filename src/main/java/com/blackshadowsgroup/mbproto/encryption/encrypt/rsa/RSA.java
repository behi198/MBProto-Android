package com.blackshadowsgroup.mbproto.encryption.encrypt.rsa;

import com.blackshadowsgroup.mbproto.encryption.binary.BinaryWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Behi198 on 10/3/2017.
 */
public class RSA {
    private static RSA instance;

    private BigInteger e;
    private BigInteger n;

    private RSA() {
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public static RSA getInstance(){
        if (instance == null)
            instance = new RSA();
        return instance;
    }

    public byte[] encryptRSA(byte[] rawBytes) {
        BinaryWriter tempWriter = new BinaryWriter();
        try {
            for (byte rawByte : rawBytes) {
                String tmp = (rawByte & 0xff) + "";
                BigInteger c = (new BigInteger(tmp)).modPow(e, n);


                tempWriter.writeString(c.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempWriter.toByteArray();

    }

    public  String encryptRSA(String rawString) {
        try {
            byte[] sequence = rawString.getBytes("UTF-8");
            return Arrays.toString(encryptRSA(sequence));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
