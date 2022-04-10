package com.example.todo.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping(path = "api/v1/todos")
public class TodoController {

    @GetMapping("/{id}")
    public ResponseEntity<String> getTodoById(@PathVariable("id") int id) {
        var random = new Random();
        var randomNumber = random.nextInt(1, 100);
        if (randomNumber >= id) {
            System.out.println("--> TodoService Returned 500 ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        System.out.println("--> TodoService Returned 200 OK");
        return new ResponseEntity<>("This is TodoService", HttpStatus.OK);
    }
}
