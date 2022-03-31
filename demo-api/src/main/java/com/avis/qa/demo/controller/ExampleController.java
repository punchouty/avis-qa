package com.avis.qa.demo.controller;

import com.avis.qa.demo.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
public class ExampleController {

    @Value(value = "${application.external.api}")
    private String externalUrl;
    private final RestTemplate restTemplate;

    public ExampleController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        this.restTemplate.setMessageConverters(messageConverters);
    }

    @GetMapping("/users/{id}")
    public HashMap getUser(@PathVariable String id) {
        String url = externalUrl + "/users/" + id;
        System.out.println("external api url : " + url);
        ResponseEntity<HashMap> responseEntity = restTemplate.getForEntity(url, HashMap.class);
        HashMap body = responseEntity.getBody();
        return body;
    }
}

