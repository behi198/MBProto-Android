package com.blackshadowsgroup.mbproto.encryption.encrypt.diffiehellman;

import com.blackshadowsgroup.mbproto.encryption.network.SessionManager;

import java.util.Arrays;


/**
 * DH class
 */
@SuppressWarnings("unused")
public class DH {

    private String serverResponse;


    private static DH instance;


    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }// setServerResponse method


    public static DH getInstance() {
        if (instance == null)
            instance = new DH();
        return instance;
    }// getInstance method


    private DH() {
    }// constructor


    public void handleResponse() {
        SessionManager.getInstance().setSharedKey(generateKey());
    }// handleResponse method


    public String generate() {
        int bitLength = 256;
        DiffieHellman dh = new DiffieHellman(bitLength).GenerateRequest();
        SessionManager.getInstance().setPrime(dh.getPrime());
        SessionManager.getInstance().setMine(dh.getMine());
        return dh.toString();
    }// generate method


    private byte[] generateKey() {
        if (serverResponse != null && !serverResponse.equals("")) {
            DiffieHellman dh = new DiffieHellman(SessionManager.getInstance().getPrime(), SessionManager.getInstance().getMine());
            dh.HandleResponse(serverResponse);
            byte[] key = dh.getKey();
            if (key.length == 33 && key[0] == 0) {
                key = Arrays.copyOfRange(key, 1, 33);
            }// if
            return key;
        }// if
        return new byte[]{};
    }// generateKey method


    public void setSharedKey(byte[] sharedKey) {
        SessionManager.getInstance().setSharedKey(sharedKey);
    }// setSharedKey method

}// DH class
