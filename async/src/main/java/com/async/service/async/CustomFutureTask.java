package com.async.service.async;

import lombok.Getter;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Getter
public class CustomFutureTask<V> extends FutureTask<V> {

    private final Long submittedTaskStartTime;

    public CustomFutureTask(Callable<V> callable, Long submittedTaskStartTime) {
        super(callable);
        this.submittedTaskStartTime = submittedTaskStartTime;
    }

    public CustomFutureTask(Runnable runnable, V result, Long submittedTaskStartTime) {
        super(runnable, result);
        this.submittedTaskStartTime = submittedTaskStartTime;
    }
}
