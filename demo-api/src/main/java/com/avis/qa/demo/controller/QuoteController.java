package com.avis.qa.demo.controller;

import com.avis.qa.demo.domain.Quote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class QuoteController {

    @Value(value = "${application.external.api}")
    private String externalUrl;
    private final RestTemplate restTemplate;

    public QuoteController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/quote")
    public Quote getQuote() {
        return restTemplate.getForObject(externalUrl, Quote.class);
    }
}
