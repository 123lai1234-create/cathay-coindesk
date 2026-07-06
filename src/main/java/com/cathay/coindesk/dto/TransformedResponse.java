package com.cathay.coindesk.dto;

import java.util.ArrayList;
import java.util.List;

public class TransformedResponse {

    private String updatedTime;
    private List<CurrencyInfo> currencies = new ArrayList<>();

    public TransformedResponse() {
    }

    public TransformedResponse(String updatedTime, List<CurrencyInfo> currencies) {
        this.updatedTime = updatedTime;
        this.currencies = currencies;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<CurrencyInfo> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyInfo> currencies) {
        this.currencies = currencies;
    }
}