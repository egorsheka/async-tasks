package com.async.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class ReportCreationService {

    private final List<ReportService> reportServices;
    private final ExecutorService executor;

    public String createReportBlockingExecutorService() {
        List<Future<String>> reportsFuture = new ArrayList<>();
        for (ReportService reportService : reportServices) {
            reportsFuture.add(executor.submit(reportService::createService));
        }
        String report_1 = null;
        String report_2 = null;
        String report_3 = null;
        try {
            report_1 = reportsFuture.get(0).get();
            report_2 = reportsFuture.get(1).get();
            report_3 = reportsFuture.get(2).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return report_1 + report_2 + report_3;
    }

    public String createReportBlockingCompletableFuture() {
        List<CompletableFuture<String>> reportsCompletableFuture = new ArrayList<>();
        for (ReportService reportService : reportServices) {
            reportsCompletableFuture.add(CompletableFuture.supplyAsync(reportService::createService));
        }
        return CompletableFuture.allOf(reportsCompletableFuture.get(0), reportsCompletableFuture.get(1),
                                       reportsCompletableFuture.get(2))
            .thenApply(result -> {

                try {
                    String report_1 = reportsCompletableFuture.get(0).get();
                    String report_2 = reportsCompletableFuture.get(1).get();
                    String report_3 = reportsCompletableFuture.get(2).get();
                    return report_1 + report_2 + report_3;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).join();
    }


}
