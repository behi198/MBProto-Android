package com.blackshadowsgroup.mbproto.encryption.encrypt.interfaces;

import com.blackshadowsgroup.mbproto.encryption.model.Result;


/**
 * ConnectionHandler interface
 * @author Behi198
 */
public interface ConnectionHandler {


    /**
     * this method is call when server response is ok
     *
     * @param result
     */
    void onResultReceived(Result result);


    /**
     * this method call when error occurred
     *
     * @param throwable WebGate throwable
     */
    void onRequestFailed(Throwable throwable);

}// ConnectionHandler interface
