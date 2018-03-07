package com.blackshadowsgroup.mbproto.encryption.network.exceptions;


/**
 * MethodBlockedException class
 */
public class MethodBlockedException extends RuntimeException {


    public MethodBlockedException() {
        super();
    }// constructor


    public MethodBlockedException(String message) {
        super(message);
    }// constructor


    public MethodBlockedException(String message, Throwable cause) {
        super(message, cause);
    }// constructor


    public MethodBlockedException(Throwable cause) {
        super(cause);
    }// constructor


}// MethodBlockedException class
