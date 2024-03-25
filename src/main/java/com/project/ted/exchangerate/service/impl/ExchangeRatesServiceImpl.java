package com.project.ted.exchangerate.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.project.ted.exchangerate.entity.Exchange;
import com.project.ted.exchangerate.repository.ExchangeRepository;
import com.project.ted.exchangerate.service.ExchangeRatesService;
import com.project.ted.exchangerate.util.JsonUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final ExchangeRepository exchangeRepository;

    public ExchangeRatesServiceImpl(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public List<Exchange> getNewestRate() {
        return exchangeRepository.findLatestExchanges();
    }

    @Override
    public void getDataFromOpenAPI() {
        System.out.println("獲取資料開始");
        try {
            URL url = URI.create("https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ArrayList<Map<String, String>> jsonDataList = JsonUtil.toObject(response.toString(), new TypeReference<>() {
            });

            if (jsonDataList == null) {
                throw new RuntimeException("獲取資料異常");
            }

            jsonDataList.forEach(data -> {
                Date createDate = new Date();
                for (String key : data.keySet()) {
                    Exchange exchange = new Exchange();
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    try {
                        if ("Date".equals(key)) {
                            createDate = dateFormat.parse(data.get(key));
                            continue;
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException("日期解析錯誤");
                    }
                    if (key.contains("/")) {
                        String[] split = key.split("/");
                        exchange.setFromCurrency(split[0]);
                        exchange.setToCurrency(split[1]);
                        exchange.setConvertResult(Double.parseDouble(data.get(key)));
                        exchange.setCreateDate(createDate);
                    }
                    this.saveExchange(exchange);
                }
            });

            System.out.println("獲取資料結束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLastUpdateTime() {
        Date lastUpdateTime = exchangeRepository.getLastUpdateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(lastUpdateTime);
    }

    private void saveExchange(Exchange exchange) {
        try {
            exchangeRepository.save(exchange);
        } catch (DataIntegrityViolationException e) {
            System.out.println(JsonUtil.toJson(exchange) + "重複入庫直接忽略");
        }
    }
}