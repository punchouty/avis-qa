package com.avis.qa.demo.controller;

import com.avis.qa.demo.domain.Quote;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class QuoteController {

    private final RestTemplate restTemplate;

    public QuoteController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/quote")
    public Quote getQuote() {
        return restTemplate.getForObject(
                "https://quoters.apps.pcfone.io/api/random", Quote.class);
    }
}
