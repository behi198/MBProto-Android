package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * UserRegisterException class
 */
@SuppressWarnings("unused")
public class UserRegisterException extends RuntimeException {


    public UserRegisterException() {
        super();
    }// constructor


    public UserRegisterException(String message) {
        super(message);
    }// constructor


    public UserRegisterException(String message, Throwable cause) {
        super(message, cause);
    }// constructor


    public UserRegisterException(Throwable cause) {
        super(cause);
    }// constructor


}// UserRegisterException class
