package com.blackshadowsgroup.mbproto.encryption.network;


import com.blackshadowsgroup.mbproto.encryption.encrypt.sha.SHA;

import java.math.BigInteger;
import java.util.Random;


/**
 * SessionManager class
 */
public class SessionManager {

    private static SessionManager instance;

    private String mobile;
    private long SESSION_ID;
    private long AUTH_KEY_ID = 2;
    private byte[] MSG_KEY = SHA.getInstance().sha128("0");
    private Long SALT;
    private String hash;
    private boolean userRegistered = false;
    private byte[] SHARED_KEY;
    private BigInteger prime;
    private BigInteger mine;
    private String PREFERENCES_NAME = "BLACKSHADOWSGROUP";


    private SessionManager() {
    }// constructor


    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }// getInstance method


    void setMobile(String mobile) {
        this.mobile = mobile;
    }// setMobile method


    void setSessionId(long SESSION_ID) {
        this.SESSION_ID = SESSION_ID;
    }// setSessionId method


    void setAuthKeyId(long AUTH_KEY_ID) {
        this.AUTH_KEY_ID = AUTH_KEY_ID;
    }// setAuthKeyId method


    void setMsgKey(byte[] MSG_KEY) {
        this.MSG_KEY = MSG_KEY;
    }// setMsgKey method


    void setSalt(Long SALT) {
        this.SALT = SALT;
    }// setSalt method


    void setHash(String hash) {
        this.hash = hash;
    }// setHash method


    void setUserRegistered(boolean userRegistered) {
        this.userRegistered = userRegistered;
    }// setUserRegistered method


    public void setSharedKey(byte[] SHARED_KEY) {
        this.SHARED_KEY = SHARED_KEY;
    }// setSharedKey method


    public void setPrime(BigInteger prime) {
        this.prime = prime;
    }// setPrime method


    public void setMine(BigInteger mine) {
        this.mine = mine;
    }// setMine method


    public void setPreferencesName(String PREFERENCES_NAME) {
        this.PREFERENCES_NAME = PREFERENCES_NAME;
    }// setPreferencesName method


    String getMobile() {
        return mobile;
    }// getMobile method


    long getSessionId() {
        return SESSION_ID;
    }// getSessionId method


    long getAuthKeyId() {
        return AUTH_KEY_ID;
    }// getAuthKeyId method


    byte[] getMsgKey() {
        return MSG_KEY;
    }// getMsgKey method


    Long getSalt() {
        return SALT;
    }// getSalt method


    String getHash() {
        return hash;
    }// getHash method


    boolean isUserRegistered() {
        return userRegistered;
    }// isUserRegistered method


    byte[] getSharedKey() {
        return SHARED_KEY;
    }// getSharedKey method


    public BigInteger getPrime() {
        return prime;
    }// getPrime method


    public BigInteger getMine() {
        return mine;
    }// getMine method


    String getPreferencesName() {
        return PREFERENCES_NAME;
    }// getPreferencesName method


    void setSalt() {
        setSalt(nextLong(new Random(), 29864438));
    }// setSalt method


    private static long nextLong(Random rng, long n) {
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }// nextLong method

}// SessionManager class
