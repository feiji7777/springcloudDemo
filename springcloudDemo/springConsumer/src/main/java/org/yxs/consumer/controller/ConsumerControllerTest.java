package org.yxs.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consumerTest")
public class ConsumerControllerTest {
    @GetMapping("/test")
    public String send() {
        System.out.println("发送请求到provider");
        return "测试";
    }
}
