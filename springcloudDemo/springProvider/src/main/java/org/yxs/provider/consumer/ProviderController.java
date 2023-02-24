package org.yxs.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yxs.client.ProviderFeignClient;
import org.yxs.provider.common.entity.Demo;
import org.yxs.provider.mapper.DemoMapper;
import org.yxs.provider.service.ProviderService;

import java.util.HashMap;
import java.util.List;

@RefreshScope
@RestController("provider")
public class ProviderController implements ProviderFeignClient {

    @Value("${providerName}")
    private String name;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private DemoMapper demoMapper;
  //  @Value("${provider.datasource.username}")
    private String username="";

    @Override
    public String send() {
        System.out.println("========发送请求到consumer========");
        String send = providerService.send();
        return "发送请求到consumer，收到的回应是：" + send;
    }

    @Override
    public String receive(String msg) {
        System.out.println(name);
        System.out.println(username);
        //List<Demo> demoList = demoMapper.getDemoList();
        String demoOne = providerService.queryDemo();
        int deleteFlag = providerService.delete();
        System.out.println(JSON.toJSONString(demoOne));
        System.out.println("deleteFlag:"+deleteFlag);
        System.out.println("========收到来自consumer的请求========");
        return "收到来自consumer的请求: " + msg;
    }


}
