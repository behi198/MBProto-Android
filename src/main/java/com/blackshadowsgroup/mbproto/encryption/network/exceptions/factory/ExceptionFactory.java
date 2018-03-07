package com.blackshadowsgroup.mbproto.encryption.network.exceptions.factory;


import com.blackshadowsgroup.mbproto.encryption.network.WebGate;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.NotFoundException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.PhoneCodeHashEmpty;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.RequestFailedException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.RequestInputArgumentException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.ServerResponseException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.VerificationCodeExpired;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.VerificationCodeInvalidException;


/**
 * ExceptionFactory class
 */
public class ExceptionFactory {


    public static Throwable getException(int errorCode, String message) {
        switch (errorCode) {
            case WebGate.ARGUMENT_ERROR:
                return new RequestInputArgumentException(message);
            case WebGate.NOT_FOUND_ERROR:
                return new NotFoundException(message);
            case WebGate.REQUEST_NOT_SEND:
                return new RequestFailedException(message);
            case WebGate.SERVER_NOT_RESPONSE:
                return new ServerResponseException(message);
            default:
                return new RequestFailedException(message);
        }
    }// getException method


    public static Throwable getVerificationException(int code, String message) {
        switch (code) {
            case WebGate.ARGUMENT_ERROR: {
                if (message != null) {
                    if (message.contains("INVALID")) {
                        return new VerificationCodeInvalidException(message);
                    } else if (message.contains("EXPIRED")) {
                        return new VerificationCodeExpired(message);
                    } else if (message.contains("EMPTY")){
                        return new PhoneCodeHashEmpty(message);
                    }//else if
                }// if
            }// case

            case WebGate.NOT_FOUND_ERROR:
                return new NotFoundException(message);

            case WebGate.REQUEST_NOT_SEND:
                return new RequestFailedException(message);

            case WebGate.SERVER_NOT_RESPONSE:
                return new ServerResponseException(message);

            default:
                return new RequestFailedException(message);
        }// switch
    }// getVerificationException method

}// ExceptionFactory class
