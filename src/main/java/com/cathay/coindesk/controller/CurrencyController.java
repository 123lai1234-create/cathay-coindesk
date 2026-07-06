package com.cathay.coindesk.controller;

import com.cathay.coindesk.entity.Currency;
import com.cathay.coindesk.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping
    public List<Currency> findAll() {
        return service.findAll();
    }

    @GetMapping("/{code}")
    public Currency findOne(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        Currency saved = service.create(currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{code}")
    public Currency update(@PathVariable String code, @RequestBody Currency currency) {
        return service.update(code, currency);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}