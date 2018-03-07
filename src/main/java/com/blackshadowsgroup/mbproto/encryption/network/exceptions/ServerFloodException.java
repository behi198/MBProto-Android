package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * ServerFloodException class
 */
@SuppressWarnings("unused")
public class ServerFloodException extends RuntimeException {


    private int waitingTime;


    public ServerFloodException() {
        super();
    }// constructor


    public ServerFloodException(String message) {
        super(message);
        if (message != null) {
            message = message.replaceAll("\"" , "");
            String[] sp = message.split("_");
            waitingTime = Integer.parseInt(sp[sp.length - 1]);
        }// if
    }// constructor


    public ServerFloodException(String message, Throwable cause) {
        super(message, cause);
    }// constructor


    public ServerFloodException(Throwable cause) {
        super(cause);
    }// constructor


    public int getWaitingTime() {
        return waitingTime;
    }// getWaitingTime method

}// ServerFloodException class
