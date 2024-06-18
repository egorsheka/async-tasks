package com.async.service.impl;

import com.async.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceMedium implements ReportService {
    @Override
    public String createService() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "report 2.\n";
    }
}
