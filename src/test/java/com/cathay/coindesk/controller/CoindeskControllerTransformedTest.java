package com.cathay.coindesk.controller;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.repository.CurrencyRepository;
import com.cathay.coindesk.service.CoindeskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 呼叫資料轉換 API 測試(作業需求 4)
 * 顯示回傳內容
 */
@SpringBootTest
@AutoConfigureMockMvc
class CoindeskControllerTransformedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoindeskService coindeskService;

    @MockBean
    private CurrencyRepository currencyRepository;

    @Test
    @DisplayName("GET /api/coindesk/transformed — 顯示轉換後內容")
    void getTransformed() throws Exception {
        // 1. 模擬外部 coindesk API 回傳
        CoindeskRawResponse raw = new CoindeskRawResponse();
        CoindeskRawResponse.Time t = new CoindeskRawResponse.Time();
        t.setUpdated("Sep 2, 2024 07:07:20 UTC");
        t.setUpdatedISO("2024-09-02T07:07:20+00:00");
        raw.setTime(t);
        raw.setChartName("Bitcoin");

        Map<String, CoindeskRawResponse.Bpi> bpi = new LinkedHashMap<>();
        CoindeskRawResponse.Bpi usd = new CoindeskRawResponse.Bpi();
        usd.setCode("USD");
        usd.setRateFloat(57756.2984);
        bpi.put("USD", usd);

        CoindeskRawResponse.Bpi gbp = new CoindeskRawResponse.Bpi();
        gbp.setCode("GBP");
        gbp.setRateFloat(43984.0203);
        bpi.put("GBP", gbp);

        CoindeskRawResponse.Bpi eur = new CoindeskRawResponse.Bpi();
        eur.setCode("EUR");
        eur.setRateFloat(52243.2865);
        bpi.put("EUR", eur);
        raw.setBpi(bpi);

        when(coindeskService.fetchRaw()).thenReturn(raw);

        // 2. 模擬 DB 中對應的中文名稱
        when(currencyRepository.findAll()).thenReturn(Arrays.asList(
                new Currency("USD", "美元"),
                new Currency("GBP", "英鎊"),
                new Currency("EUR", "歐元")
        ));

        // 3. 打 API + 印出內容
        mockMvc.perform(get("/api/coindesk/transformed"))
                .andDo(print())
                .andExpect(status().isOk())
                // A. 更新時間格式
                .andExpect(jsonPath("$.updatedTime").value("2024/09/02 07:07:20"))
                // B. 幣別清單
                .andExpect(jsonPath("$.currencies.length()").value(3))
                .andExpect(jsonPath("$.currencies[0].code").value("USD"))
                .andExpect(jsonPath("$.currencies[0].chineseName").value("美元"))
                .andExpect(jsonPath("$.currencies[0].rate").value(57756.2984))
                .andExpect(jsonPath("$.currencies[1].code").value("GBP"))
                .andExpect(jsonPath("$.currencies[1].chineseName").value("英鎊"))
                .andExpect(jsonPath("$.currencies[2].code").value("EUR"))
                .andExpect(jsonPath("$.currencies[2].chineseName").value("歐元"));
    }
}