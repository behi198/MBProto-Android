package com.blackshadowsgroup.mbproto.encryption.threads;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Behi198 on 9/27/2017.
 */

public class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public PriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }
    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {

                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }
}
