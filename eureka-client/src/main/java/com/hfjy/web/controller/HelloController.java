package com.hfjy.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hello")
@RestController
public class HelloController extends BaseController{

    @GetMapping("/hi")
    public String hello() {
        getHeaders();
        return "hello";
    }

    @GetMapping("/login")
    public boolean login() {
        return true;
    }
}
