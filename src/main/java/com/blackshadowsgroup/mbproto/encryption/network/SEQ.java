package com.blackshadowsgroup.mbproto.encryption.network;

import android.util.Log;


import com.blackshadowsgroup.mbproto.encryption.encrypt.preferences.MySharedPreferences;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * SEQ class
 */
class SEQ {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();

    static synchronized long getSequenceNumber() {
        long now = System.nanoTime();
        while (true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now)) {
                return now;
            }// if
        }// while
    }// getSequenceNumber method

}// SEQ class
