package com.cathay.coindesk.controller;

import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.exception.ResourceNotFoundException;
import com.cathay.coindesk.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 幣別 CRUD API 測試(作業需求 2)
 * 顯示每個 API 回傳的內容
 */
@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    @Test
    @DisplayName("GET /api/currencies — 查詢全部")
    void findAll() throws Exception {
        when(currencyService.findAll()).thenReturn(Arrays.asList(
                new Currency("USD", "美元"),
                new Currency("GBP", "英鎊"),
                new Currency("EUR", "歐元")
        ));

        mockMvc.perform(get("/api/currencies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].code").value("USD"))
                .andExpect(jsonPath("$[0].chineseName").value("美元"));
    }

    @Test
    @DisplayName("GET /api/currencies/{code} — 查詢單筆")
    void findOne() throws Exception {
        when(currencyService.findByCode("USD")).thenReturn(new Currency("USD", "美元"));

        mockMvc.perform(get("/api/currencies/USD"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"))
                .andExpect(jsonPath("$.chineseName").value("美元"));
    }

    @Test
    @DisplayName("GET /api/currencies/{code} — 查詢不存在的幣別回 404")
    void findOne_notFound() throws Exception {
        when(currencyService.findByCode("XXX")).thenThrow(new ResourceNotFoundException("Currency not found: XXX"));

        mockMvc.perform(get("/api/currencies/XXX"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Currency not found: XXX"));
    }

    @Test
    @DisplayName("POST /api/currencies — 新增幣別,回 201")
    void create() throws Exception {
        Currency cny = new Currency("CNY", "人民幣");
        when(currencyService.create(any(Currency.class))).thenReturn(cny);

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cny)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CNY"))
                .andExpect(jsonPath("$.chineseName").value("人民幣"));
    }

    @Test
    @DisplayName("POST /api/currencies — 新增已存在的幣別回 400")
    void create_duplicate() throws Exception {
        Currency dup = new Currency("USD", "美元");
        when(currencyService.create(any(Currency.class)))
                .thenThrow(new IllegalArgumentException("Currency already exists: USD"));

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dup)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency already exists: USD"));
    }

    @Test
    @DisplayName("PUT /api/currencies/{code} — 修改幣別")
    void update() throws Exception {
        Currency updated = new Currency("USD", "美利堅合眾國美元");
        when(currencyService.update(any(String.class), any(Currency.class))).thenReturn(updated);

        mockMvc.perform(put("/api/currencies/USD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Currency("USD", "美利堅合眾國美元"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"))
                .andExpect(jsonPath("$.chineseName").value("美利堅合眾國美元"));
    }

    @Test
    @DisplayName("DELETE /api/currencies/{code} — 刪除幣別,回 204")
    void delete_ok() throws Exception {
        // service.delete 預設不回傳值,只拋例外表示失敗
        mockMvc.perform(delete("/api/currencies/USD"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/currencies/{code} — 刪除不存在的幣別回 404")
    void delete_whenNotFound() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Currency not found: XXX"))
                .when(currencyService).delete("XXX");

        mockMvc.perform(delete("/api/currencies/XXX"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("邊界:查詢空清單")
    void findAll_empty() throws Exception {
        when(currencyService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/currencies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}