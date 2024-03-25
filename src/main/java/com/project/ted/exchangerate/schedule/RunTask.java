package com.project.ted.exchangerate.schedule;

import com.project.ted.exchangerate.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RunTask {
    @Autowired
    ExchangeRatesService exchangeRatesService;

    @Scheduled(cron = "0 0 * ? * * *") //每小時調度一次發送
    public void fetchExchangeRateData() {
        System.out.println("開始獲取資料");
        exchangeRatesService.getDataFromOpenAPI();
        System.out.println("獲取資料結束");
    }
}
