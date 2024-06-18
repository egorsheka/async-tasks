package com.async.service;

import com.async.service.async.CustomCallerRunsPolicy;
import com.async.service.async.StatisticsThreadPoolExecutor;
import com.async.service.impl.ReportServiceHigh;
import com.async.service.impl.ReportServiceLow;
import com.async.service.impl.ReportServiceMedium;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportCreationServiceTest {
    public static int NUMBER_OF_THREADS = 30;
    public static AtomicInteger counter = new AtomicInteger(0);


    @Test
    public void testCompletableFuture() throws InterruptedException {
        CountDownLatch countDownLatch = runCompletableFutureForkJoinPool();
        countDownLatch.await(60, TimeUnit.SECONDS);
        Assertions.assertEquals(0, countDownLatch.getCount());
    }

    @Test
    public void testLinkedBlockingQueue() throws InterruptedException {
        final CustomCallerRunsPolicy customCallerRunsPolicy = new CustomCallerRunsPolicy();
        StatisticsThreadPoolExecutor statisticsThreadPoolExecutor =
            new StatisticsThreadPoolExecutor(10, 100, 3L,
                                             TimeUnit.SECONDS, new LinkedBlockingQueue<>(50),
                                             new CustomCallerRunsPolicy());
        CountDownLatch countDownLatch = runThreadPoolExecutor(statisticsThreadPoolExecutor, customCallerRunsPolicy);
        countDownLatch.await(60, TimeUnit.SECONDS);
        Thread.sleep(4000);
        System.out.println(statisticsThreadPoolExecutor.getTimeReport());

        Assertions.assertEquals(0, countDownLatch.getCount());
    }

    @Test
    public void testSynchronousQueue() throws InterruptedException {
        final CustomCallerRunsPolicy customCallerRunsPolicy = new CustomCallerRunsPolicy();
        StatisticsThreadPoolExecutor statisticsThreadPoolExecutor =
            new StatisticsThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                                             new SynchronousQueue<Runnable>());

        CountDownLatch countDownLatch = runThreadPoolExecutor(statisticsThreadPoolExecutor, customCallerRunsPolicy);

        countDownLatch.await(60, TimeUnit.SECONDS);
        Assertions.assertEquals(0, countDownLatch.getCount());
    }


    public CountDownLatch runThreadPoolExecutor(StatisticsThreadPoolExecutor statisticsThreadPoolExecutor,
                                                CustomCallerRunsPolicy customCallerRunsPolicy) {
        statisticsThreadPoolExecutor.setRejectedExecutionHandler(customCallerRunsPolicy);
        List<ReportService> reportServices = new ArrayList<>();
        reportServices.add(new ReportServiceLow());
        reportServices.add(new ReportServiceMedium());
        reportServices.add(new ReportServiceHigh());
        ReportCreationService reportCreationService =
            new ReportCreationService(reportServices, statisticsThreadPoolExecutor);

        CountDownLatch tasksLatch = new CountDownLatch(1);
        CountDownLatch finishedTaskLatch = new CountDownLatch(NUMBER_OF_THREADS);

        ExecutorService runReportTasksExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Callable<String> task = () -> {
            try {
                tasksLatch.await();
                return reportCreationService.createReportBlockingExecutorService();
            } catch (Throwable e) {
                System.out.println(e);
                return null;
            } finally {
                counter.incrementAndGet();
                finishedTaskLatch.countDown();
            }
        };

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            runReportTasksExecutor.submit(task);
        }
        tasksLatch.countDown();
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    System.out.println("failed count tasks = " + customCallerRunsPolicy.getFailedTaskCount());
                    System.out.println(statisticsThreadPoolExecutor.getSizesInfo());
                    System.out.println();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return finishedTaskLatch;
    }

    public CountDownLatch runCompletableFutureForkJoinPool() {
        List<ReportService> reportServices = new ArrayList<>();
        reportServices.add(new ReportServiceLow());
        reportServices.add(new ReportServiceMedium());
        reportServices.add(new ReportServiceHigh());
        ReportCreationService reportCreationService =
            new ReportCreationService(reportServices, null);

        CountDownLatch tasksLatch = new CountDownLatch(1);
        CountDownLatch finishedTaskLatch = new CountDownLatch(NUMBER_OF_THREADS);

        ExecutorService runReportTasksExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Callable<String> task = () -> {
            try {
                tasksLatch.await();
                return reportCreationService.createReportBlockingCompletableFuture();
            } catch (Throwable e) {
                System.out.println(e);
                return null;
            } finally {
                counter.incrementAndGet();
                finishedTaskLatch.countDown();
            }
        };

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            runReportTasksExecutor.submit(task);
        }
        tasksLatch.countDown();
        return finishedTaskLatch;
    }

}
