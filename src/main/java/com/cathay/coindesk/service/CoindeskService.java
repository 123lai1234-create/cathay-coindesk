package com.cathay.coindesk.service;

import com.cathay.coindesk.dto.CoindeskRawResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoindeskService {

    private final RestTemplate restTemplate;
    private final String coindeskApiUrl;

    public CoindeskService(RestTemplate restTemplate,
                           @Value("${coindesk.api.url}") String coindeskApiUrl) {
        this.restTemplate = restTemplate;
        this.coindeskApiUrl = coindeskApiUrl;
    }

    public CoindeskRawResponse fetchRaw() {
        return restTemplate.getForObject(coindeskApiUrl, CoindeskRawResponse.class);
    }
}