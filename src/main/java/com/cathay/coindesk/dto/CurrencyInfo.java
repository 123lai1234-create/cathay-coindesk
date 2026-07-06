package com.cathay.coindesk.dto;

public class CurrencyInfo {

    private String code;
    private String chineseName;
    private Double rate;

    public CurrencyInfo() {
    }

    public CurrencyInfo(String code, String chineseName, Double rate) {
        this.code = code;
        this.chineseName = chineseName;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}