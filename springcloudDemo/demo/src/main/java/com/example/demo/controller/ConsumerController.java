package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consunerController")
public class ConsumerController {

    @Value("${providerName}")
    private String name;
    @GetMapping("/test")
    public String send() {
        System.out.println(name);
        return "测试";
    }
}
