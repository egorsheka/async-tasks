package com.async.service.async;

import lombok.Getter;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomCallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {

    @Getter
    private final AtomicInteger failedTaskCount = new AtomicInteger(0);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        failedTaskCount.incrementAndGet();
        super.rejectedExecution(r, e);
    }
}
