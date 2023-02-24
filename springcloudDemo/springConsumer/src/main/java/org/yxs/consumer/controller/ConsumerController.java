package org.yxs.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yxs.client.ConsumerFeignClient;
import org.yxs.consumer.service.ConsumerService;

@RestController
@RequestMapping("consumer")
public class ConsumerController implements ConsumerFeignClient
        {

    @Autowired
    private ConsumerService consumerService;

    @Override
    @GetMapping("/send")
    public String send() {
        System.out.println("发送请求到provider");
        String send = consumerService.send();
        return "发送请求到provider: " + send;
    }

    @Override
    @GetMapping("/receive")
    public String receive(String msg) {
        System.out.println("收到来自provider的请求，收到的消息是: " + msg);
        return "收到来自provider的请求，收到的消息是：" + msg;
    }
}
