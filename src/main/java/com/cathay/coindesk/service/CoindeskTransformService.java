package com.cathay.coindesk.service;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import com.cathay.coindesk.dto.CurrencyInfo;
import com.cathay.coindesk.dto.TransformedResponse;
import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoindeskTransformService {

    private static final DateTimeFormatter TARGET_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final String UNKNOWN_CURRENCY_NAME = "未知幣別";

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CoindeskTransformService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    /**
     * 將 coindesk 原始資料轉換為:
     * A. 更新時間(yyyy/MM/dd HH:mm:ss)
     * B. 幣別清單(幣別代碼、中文名稱、匯率)
     */
    public TransformedResponse transform(CoindeskRawResponse raw) {
        if (raw == null || raw.getTime() == null || raw.getBpi() == null) {
            throw new IllegalArgumentException("Invalid coindesk response: missing time or bpi");
        }

        String updatedTime = parseTime(raw.getTime().getUpdatedISO());

        Map<String, String> nameMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCode, Currency::getChineseName));

        List<CurrencyInfo> list = new ArrayList<>();
        for (CoindeskRawResponse.Bpi bpi : raw.getBpi().values()) {
            String code = bpi.getCode();
            String chineseName = nameMap.getOrDefault(code, UNKNOWN_CURRENCY_NAME);
            list.add(new CurrencyInfo(code, chineseName, bpi.getRateFloat()));
        }

        return new TransformedResponse(updatedTime, list);
    }

    /**
     * 將 ISO 8601 時間轉成 yyyy/MM/dd HH:mm:ss。
     * 若解析失敗,回傳原字串(不丟例外,讓上層決定怎麼處理)。
     */
    public String parseTime(String iso) {
        if (iso == null || iso.isEmpty()) {
            return "";
        }
        try {
            return OffsetDateTime.parse(iso).format(TARGET_FORMAT);
        } catch (Exception e) {
            return iso;
        }
    }
}