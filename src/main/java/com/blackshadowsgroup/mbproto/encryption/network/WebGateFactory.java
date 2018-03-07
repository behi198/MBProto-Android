package com.blackshadowsgroup.mbproto.encryption.network;

import android.content.Context;

/**
 * WebGateFactory class
 * @author Behi198
 */
@SuppressWarnings("unused")
public final class WebGateFactory {


    public static WebGate getWebGate(Context context, String url) {
        WebGateImpl.Config.begin()
                .initiateWith(context)
                .setUrl(url)
                .createGate();
        WebGateImpl impl = WebGateImpl.getInstance();
        impl.retrieveSession();
        return impl;
    }// getWebGate method

}// WebGateFactory class
