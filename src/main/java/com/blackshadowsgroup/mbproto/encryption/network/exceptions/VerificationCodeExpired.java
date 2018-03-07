package com.blackshadowsgroup.mbproto.encryption.network.exceptions;


/**
 * VerificationCodeExpired class
 */
public class VerificationCodeExpired extends RuntimeException {


    public VerificationCodeExpired() {
        super();
    }// constructor


    public VerificationCodeExpired(String message) {
        super(message);
    }// constructor


    public VerificationCodeExpired(String message, Throwable cause) {
        super(message, cause);
    }// constructor


    public VerificationCodeExpired(Throwable cause) {
        super(cause);
    }// constructor


}// VerificationCodeExpired class
