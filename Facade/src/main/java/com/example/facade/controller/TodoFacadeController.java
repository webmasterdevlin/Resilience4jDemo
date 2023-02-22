package com.example.facade.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(path = "api/v1/facade/todos")
public class TodoFacadeController {
    @Lazy
    private final RestTemplate restTemplate;

    private static final String URL = "http://localhost:8082/api/v1/todos";
    private static final String INSTANCE = "facade";

    public TodoFacadeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retry(name = INSTANCE)
    @CircuitBreaker(name = INSTANCE)
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") int id) {
        try {
            System.out.println("--> FacadeService SENT Request");
            var response = restTemplate.getForObject("%s/%s".formatted(URL, id), String.class);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("--> FacadeService Returned 500 ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
