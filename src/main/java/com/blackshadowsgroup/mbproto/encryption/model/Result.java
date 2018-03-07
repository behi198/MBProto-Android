package com.blackshadowsgroup.mbproto.encryption.model;

import java.io.ByteArrayOutputStream;


/**
 * Result class
 * @author Behi198
 */
public class Result {

    private int code;
    private String message;
    private ByteArrayOutputStream stream;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }

    public Result() {
        this.code = 1000;
        stream = new ByteArrayOutputStream();
        message = "Request not sent";
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result(int code, String message, ByteArrayOutputStream stream) {
        this.code = code;
        this.message = message;
        this.stream = stream;
    }

}// Result class

