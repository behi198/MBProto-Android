package com.blackshadowsgroup.mbproto.encryption.encrypt.aes;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES {


    private byte[] key_Array;


    public AES(byte[] key_Array) {
        this.key_Array = key_Array;
    }// constructor


    public byte[] encrypt(byte[] bytesToEncrypt) {
        try {
            Cipher _Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            byte[] iv = {1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Key SecretKey = new SecretKeySpec(key_Array, "AES");
            _Cipher.init(Cipher.ENCRYPT_MODE, SecretKey, ivspec);
            return _Cipher.doFinal(bytesToEncrypt);
        } catch (Exception e) {
            System.out.println("[Exception]:" + e.getMessage());
        }
        return new byte[]{};
    }// encrypt method


    public byte[] decrypt(byte[] EncryptedMessage) {
        try {
            Cipher _Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            byte[] iv = {1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Key SecretKey = new SecretKeySpec(key_Array, "AES");
            _Cipher.init(Cipher.DECRYPT_MODE, SecretKey, ivspec);

            return _Cipher.doFinal(EncryptedMessage);

        } catch (Exception e) {
            System.out.println("[Exception]:" + e.getMessage());
        }
        return new byte[]{};
    }// decrypt method

}// AES class

