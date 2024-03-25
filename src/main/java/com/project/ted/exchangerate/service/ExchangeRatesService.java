package com.project.ted.exchangerate.service;

import com.project.ted.exchangerate.entity.Exchange;

import java.util.List;

public interface ExchangeRatesService {
    List<Exchange> getNewestRate();
    void getDataFromOpenAPI();
    String getLastUpdateTime();
}
