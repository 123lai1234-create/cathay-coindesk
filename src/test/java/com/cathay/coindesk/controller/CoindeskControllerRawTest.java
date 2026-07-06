package com.cathay.coindesk.controller;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import com.cathay.coindesk.service.CoindeskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 呼叫 coindesk API 測試(作業需求 3)
 * 顯示回傳內容
 */
@SpringBootTest
@AutoConfigureMockMvc
class CoindeskControllerRawTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoindeskService coindeskService;

    @Test
    @DisplayName("GET /api/coindesk/raw — 顯示 coindesk 原始 API 內容")
    void getRaw() throws Exception {
        CoindeskRawResponse mock = new CoindeskRawResponse();

        CoindeskRawResponse.Time t = new CoindeskRawResponse.Time();
        t.setUpdated("Sep 2, 2024 07:07:20 UTC");
        t.setUpdatedISO("2024-09-02T07:07:20+00:00");
        t.setUpdateduk("Sep 2, 2024 at 08:07 BST");
        mock.setTime(t);
        mock.setDisclaimer("just for test");
        mock.setChartName("Bitcoin");

        Map<String, CoindeskRawResponse.Bpi> bpi = new LinkedHashMap<>();
        CoindeskRawResponse.Bpi usd = new CoindeskRawResponse.Bpi();
        usd.setCode("USD");
        usd.setSymbol("$");
        usd.setRate("57,756.298");
        usd.setDescription("United States Dollar");
        usd.setRateFloat(57756.2984);
        bpi.put("USD", usd);

        CoindeskRawResponse.Bpi gbp = new CoindeskRawResponse.Bpi();
        gbp.setCode("GBP");
        gbp.setSymbol("£");
        gbp.setRate("43,984.02");
        gbp.setDescription("British Pound Sterling");
        gbp.setRateFloat(43984.0203);
        bpi.put("GBP", gbp);

        CoindeskRawResponse.Bpi eur = new CoindeskRawResponse.Bpi();
        eur.setCode("EUR");
        eur.setSymbol("€");
        eur.setRate("52,243.287");
        eur.setDescription("Euro");
        eur.setRateFloat(52243.2865);
        bpi.put("EUR", eur);
        mock.setBpi(bpi);

        when(coindeskService.fetchRaw()).thenReturn(mock);

        mockMvc.perform(get("/api/coindesk/raw"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time.updatedISO").value("2024-09-02T07:07:20+00:00"))
                .andExpect(jsonPath("$.disclaimer").value("just for test"))
                .andExpect(jsonPath("$.chartName").value("Bitcoin"))
                .andExpect(jsonPath("$.bpi.USD.code").value("USD"))
                .andExpect(jsonPath("$.bpi.USD.rate_float").value(57756.2984))
                .andExpect(jsonPath("$.bpi.GBP.code").value("GBP"))
                .andExpect(jsonPath("$.bpi.EUR.code").value("EUR"));
    }
}