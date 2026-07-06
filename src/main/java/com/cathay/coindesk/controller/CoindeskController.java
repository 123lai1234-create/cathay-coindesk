package com.cathay.coindesk.controller;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import com.cathay.coindesk.dto.TransformedResponse;
import com.cathay.coindesk.service.CoindeskService;
import com.cathay.coindesk.service.CoindeskTransformService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coindesk")
public class CoindeskController {

    private final CoindeskService coindeskService;
    private final CoindeskTransformService transformService;

    public CoindeskController(CoindeskService coindeskService,
                              CoindeskTransformService transformService) {
        this.coindeskService = coindeskService;
        this.transformService = transformService;
    }

    /**
     * 原始 coindesk API 內容
     */
    @GetMapping("/raw")
    public CoindeskRawResponse getRaw() {
        return coindeskService.fetchRaw();
    }

    /**
     * 轉換後的 API:
     * - updatedTime: yyyy/MM/dd HH:mm:ss
     * - currencies: [{code, chineseName, rate}]
     */
    @GetMapping("/transformed")
    public TransformedResponse getTransformed() {
        CoindeskRawResponse raw = coindeskService.fetchRaw();
        return transformService.transform(raw);
    }
}