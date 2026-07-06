package com.cathay.coindesk.service;

import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.exception.ResourceNotFoundException;
import com.cathay.coindesk.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public List<Currency> findAll() {
        return repository.findAll();
    }

    public Currency findByCode(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));
    }

    public Currency create(Currency currency) {
        if (repository.existsById(currency.getCode())) {
            throw new IllegalArgumentException("Currency already exists: " + currency.getCode());
        }
        return repository.save(currency);
    }

    public Currency update(String code, Currency currency) {
        Currency existing = findByCode(code);
        existing.setChineseName(currency.getChineseName());
        return repository.save(existing);
    }

    public void delete(String code) {
        if (!repository.existsById(code)) {
            throw new ResourceNotFoundException("Currency not found: " + code);
        }
        repository.deleteById(code);
    }
}