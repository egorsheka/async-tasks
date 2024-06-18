package com.async.service.impl;

import com.async.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceHigh implements ReportService {
    @Override
    public String createService() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "report 3.\n";
    }
}
