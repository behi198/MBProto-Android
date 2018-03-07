package com.blackshadowsgroup.mbproto.encryption.network;

import com.blackshadowsgroup.mbproto.encryption.binary.BinaryWriter;
import com.blackshadowsgroup.mbproto.encryption.encrypt.interfaces.ConnectionHandler;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.RequestFailedException;

import java.io.IOException;

/**
 * BaseRequest class
 * @author Behi198
 */
@SuppressWarnings("unused")
public abstract class BaseRequest {

    private final int code;
    private BinaryWriter writer;
    private ConnectionHandler handler;


    public BaseRequest(ConnectionHandler handler) {
        this.handler = handler;
        code = getCode();
        writer = new BinaryWriter();
    }// constructor


    protected BaseRequest writeInt32(int value) {
        try {
            writer.writeInt32(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeInt32 method


    protected BaseRequest writeInt64(long value) {
        try {
            writer.writeInt64(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeInt64 method


    protected BaseRequest writeString(String value) {
        try {
            writer.writeString(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeString method


    protected BaseRequest writeByte(byte value) {
        try {
            writer.writeByte(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeByte method


    protected BaseRequest writeByteArray(byte[] value) {
        try {
            writer.writeByteArray(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeByteArray method


    protected BaseRequest writeInt32AtFirst(int value) {
        try {
            writer.writeInt32Before(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeInt32AtFirst method


    protected BaseRequest writeInt64AtFirst(long value) {
        try {
            writer.writeInt64Before(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeInt64AtFirst method


    protected BaseRequest writeStringAtFirst(String value) {
        try {
            writer.writeStringBefore(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeStringAtFirst method


    protected BaseRequest writeByteAtFirst(byte value) {
        try {
            writer.writeByteBefore(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeByteAtFirst method


    protected BaseRequest writeByteArrayAtFirst(byte[] value) {
        try {
            writer.writeByteArrayBefore(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeByteArrayAtFirst method


    protected BaseRequest writeDouble(double value) {
        try {
            writer.writeDouble(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeDouble method


    protected BaseRequest writeDoubleAtFirst(double value) {
        try {
            writer.writeDoubleBefore(value);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return this;
    }// writeDoubleAtFirst method


    /**
     * @return specific request code
     */
    public abstract int getCode();


    byte[] getBody() {
        if (code == 0) {
            handler.onRequestFailed(new RequestFailedException("Request not found."));
            return new byte[]{};
        }// if
        try {
            writer.writeInt32Before(code);
            return writer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null)
                handler.onRequestFailed(new RequestFailedException("Request not created"));
        }// catch
        return new byte[]{};
    }// getBody method


    ConnectionHandler getHandler() {
        return handler;
    }// getHandler method


}// BaseRequest class
