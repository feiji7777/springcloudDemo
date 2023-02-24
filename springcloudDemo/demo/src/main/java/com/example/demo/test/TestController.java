package com.example.demo.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {


    private static final int _1MB= 1024 * 1024;  //约1m

    @GetMapping("/testDemo")
    public void testDemo() {
        //总共约8m多，堆大小设置不超过8388608B即8.388608m就会内存溢出，但是需要整数，小于8M就会重现这个错误
      List<Order> orderList = new ArrayList<>();
      while(1==1) {
          Order order = new Order();
          orderList.add(order);
      }
    }
}
