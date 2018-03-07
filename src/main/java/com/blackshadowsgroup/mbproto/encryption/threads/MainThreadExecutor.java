package com.blackshadowsgroup.mbproto.encryption.threads;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by Behi198 on 9/27/2017.
 */

public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
