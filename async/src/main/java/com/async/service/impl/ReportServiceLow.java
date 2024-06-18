package com.async.service.impl;


import com.async.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceLow implements ReportService {
    @Override
    public String createService() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "report 1.\n";
    }
}
