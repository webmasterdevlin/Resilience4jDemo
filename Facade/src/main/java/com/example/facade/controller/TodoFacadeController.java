package com.example.facade.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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

    @SneakyThrows
    @Retry(name = INSTANCE)
    @CircuitBreaker(name = INSTANCE)
    @RateLimiter(name = INSTANCE)
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") int id) {
        String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
        var circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        var state = circuitBreakerRegistry.circuitBreaker(INSTANCE).getState();
        var response = restTemplate.getForObject("%s/%s".formatted(URL, id), String.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> cbFallback(Exception e) {
        var message = e.getMessage().contains(INSTANCE)
                                    ? e.getMessage()
                                    : "Please try again later";
        return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ResponseEntity<?> retryFallback(Exception e) {
        var message = e.getMessage().contains(INSTANCE)
                ? e.getMessage()
                : "Could not connect to todo service after 5 attempts";
        return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
