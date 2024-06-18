package com.async.service.async;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StatisticsThreadPoolExecutor extends ThreadPoolExecutor {

    private final Map<Runnable, Long> executeTaskStartTime = new ConcurrentHashMap<>();
    @Getter
    private final List<Long> tasksTimesInQueueList = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<Long> executionTimes = Collections.synchronizedList(new ArrayList<>());


    public StatisticsThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public StatisticsThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public StatisticsThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new CustomFutureTask<>(callable, System.currentTimeMillis());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        long currentTime = System.currentTimeMillis();
        if (r instanceof CustomFutureTask<?> customFutureTask) {
            Long submittedTaskStartTime = customFutureTask.getSubmittedTaskStartTime();
            if (submittedTaskStartTime != null) {
                long timeInQueue = currentTime - submittedTaskStartTime;
                tasksTimesInQueueList.add(timeInQueue);
            }
        }
        executeTaskStartTime.put(r, currentTime);
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Long executeStartTime = executeTaskStartTime.get(r);
        if (executeStartTime != null) {
            long executionTime = System.currentTimeMillis() - executeStartTime;
            executionTimes.add(executionTime);
        }
        super.afterExecute(r, t);
    }

    public String getTimeReport() {
        double averageTaskTimeInQueue = tasksTimesInQueueList.stream().mapToLong(a -> a).average().orElse(0.0);
        double averageTaskTimeExecution = executionTimes.stream().mapToLong(a -> a).average().orElse(0.0);

        return "average task time in queue  = " + averageTaskTimeInQueue + "\n"
                + "averageExecution = " + averageTaskTimeExecution;
    }

    public String getSizesInfo() {
        return "queue size = " + this.getQueue().size() + "\n" +
               "thread pool size = " + this.getPoolSize();
    }
}
