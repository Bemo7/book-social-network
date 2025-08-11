package com.bemojr.book_network.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("demo")
public class DemoController {

    @GetMapping("")
    public ResponseEntity<?> getMessage(){
        return ResponseEntity.ok("Hello World");
    }
}
