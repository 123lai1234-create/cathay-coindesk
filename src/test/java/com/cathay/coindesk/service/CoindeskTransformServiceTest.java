package com.cathay.coindesk.service;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import com.cathay.coindesk.dto.TransformedResponse;
import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 資料轉換相關邏輯單元測試(作業需求 1)
 */
@ExtendWith(MockitoExtension.class)
class CoindeskTransformServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CoindeskTransformService service;

    private CoindeskRawResponse raw;

    @BeforeEach
    void setUp() {
        // 模擬 coindesk 原始回傳
        raw = new CoindeskRawResponse();

        CoindeskRawResponse.Time time = new CoindeskRawResponse.Time();
        time.setUpdated("Sep 2, 2024 07:07:20 UTC");
        time.setUpdatedISO("2024-09-02T07:07:20+00:00");
        time.setUpdateduk("Sep 2, 2024 at 08:07 BST");
        raw.setTime(time);

        raw.setDisclaimer("just for test");
        raw.setChartName("Bitcoin");

        Map<String, CoindeskRawResponse.Bpi> bpi = new LinkedHashMap<>();
        bpi.put("USD", buildBpi("USD", "$", "57,756.298", "United States Dollar", 57756.2984));
        bpi.put("GBP", buildBpi("GBP", "£", "43,984.02", "British Pound Sterling", 43984.0203));
        bpi.put("EUR", buildBpi("EUR", "€", "52,243.287", "Euro", 52243.2865));
        raw.setBpi(bpi);
        // 注意:currencyRepository.findAll() 的 mock 移到會用到它的測試裡(setUp 全設會引發 UnnecessaryStubbing)
    }

    private CoindeskRawResponse.Bpi buildBpi(String code, String symbol, String rate, String desc, Double rateFloat) {
        CoindeskRawResponse.Bpi b = new CoindeskRawResponse.Bpi();
        b.setCode(code);
        b.setSymbol(symbol);
        b.setRate(rate);
        b.setDescription(desc);
        b.setRateFloat(rateFloat);
        return b;
    }

    @Test
    @DisplayName("parseTime: 將 ISO 8601 時間轉成 yyyy/MM/dd HH:mm:ss")
    void parseTime_validIso() {
        String result = service.parseTime("2024-09-02T07:07:20+00:00");
        assertThat(result).isEqualTo("2024/09/02 07:07:20");
    }

    @Test
    @DisplayName("parseTime: 不同時區的 ISO 都能解析")
    void parseTime_differentTimezone() {
        String result = service.parseTime("2024-09-02T15:07:20+08:00");
        assertThat(result).isEqualTo("2024/09/02 15:07:20");
    }

    @Test
    @DisplayName("parseTime: null 或空字串回空字串")
    void parseTime_emptyOrNull() {
        assertThat(service.parseTime(null)).isEmpty();
        assertThat(service.parseTime("")).isEmpty();
    }

    @Test
    @DisplayName("parseTime: 無法解析時回傳原字串(不丟例外)")
    void parseTime_invalidInput() {
        assertThat(service.parseTime("not-a-date")).isEqualTo("not-a-date");
    }

    @Test
    @DisplayName("transform: 完整轉換 — 更新時間格式正確、幣別順序保持、缺中文名稱 fallback 為『未知幣別』")
    void transform_full() {
        // 模擬 DB 中已有的中文名稱(USD/EUR 有,GBP 故意缺,測 fallback)
        when(currencyRepository.findAll()).thenReturn(Arrays.asList(
                new Currency("USD", "美元"),
                new Currency("EUR", "歐元"),
                new Currency("JPY", "日圓"),
                new Currency("TWD", "新台幣")
        ));

        TransformedResponse out = service.transform(raw);

        // A. 時間格式
        assertThat(out.getUpdatedTime()).isEqualTo("2024/09/02 07:07:20");

        // B. 幣別清單
        assertThat(out.getCurrencies()).hasSize(3);
        // 順序應跟 bpi 內 LinkedHashMap 一致(USD → GBP → EUR)
        assertThat(out.getCurrencies().get(0).getCode()).isEqualTo("USD");
        assertThat(out.getCurrencies().get(0).getChineseName()).isEqualTo("美元");
        assertThat(out.getCurrencies().get(0).getRate()).isEqualTo(57756.2984);

        // GBP 故意沒建中文名稱,應 fallback 為「未知幣別」
        assertThat(out.getCurrencies().get(1).getCode()).isEqualTo("GBP");
        assertThat(out.getCurrencies().get(1).getChineseName()).isEqualTo("未知幣別");

        assertThat(out.getCurrencies().get(2).getCode()).isEqualTo("EUR");
        assertThat(out.getCurrencies().get(2).getChineseName()).isEqualTo("歐元");
    }

    @Test
    @DisplayName("transform: 缺少 time 區段時拋 IllegalArgumentException")
    void transform_missingTime() {
        CoindeskRawResponse bad = new CoindeskRawResponse();
        bad.setBpi(Collections.emptyMap());
        try {
            service.transform(bad);
            assertThat(false).as("應丟 IllegalArgumentException").isTrue();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Invalid coindesk response");
        }
    }

    @Test
    @DisplayName("transform: raw 為 null 時拋 IllegalArgumentException")
    void transform_nullInput() {
        try {
            service.transform(null);
            assertThat(false).as("應丟 IllegalArgumentException").isTrue();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Invalid coindesk response");
        }
    }
}