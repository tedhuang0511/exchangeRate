package com.project.ted.exchangerate.controller;

import com.project.ted.exchangerate.entity.Exchange;
import com.project.ted.exchangerate.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TestController {
    @Autowired
    ExchangeRatesService exchangeRatesService;

    @RequestMapping
    public String getAll(Model model) {
        List<Exchange> newestRate = exchangeRatesService.getNewestRate();
        String lastUpdateTime = exchangeRatesService.getLastUpdateTime();
        model.addAttribute("lastUpdateTime", lastUpdateTime);
        model.addAttribute("exchangeRates", newestRate);
        return "index";
    }

    @RequestMapping(value = "/fetchData", method = RequestMethod.GET)
    @ResponseBody
    public String fetchData() {
        exchangeRatesService.getDataFromOpenAPI();
        return "OK";
    }
}
