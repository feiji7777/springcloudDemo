package org.yxs.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yxs.fallback.DefaultFallback;


/**
 * @desc name-调用的服务名称，指向配置：spring.application.name
 * contextId-上下文id
 * url-一般用于调试，可以手动指定@FeignClient调用的地址
 * fallback-熔断处理类
 */
@FeignClient(name = "provider", fallback = DefaultFallback.class)
public interface ProviderFeignClient {

    /**
     * 发送请求
     * @return
     */
    @GetMapping("send")
    String send();

    /**
     * 收到请求
     * @param msg
     * @return
     */
    @GetMapping("/receive")
    String receive(@RequestParam("msg") String msg);

}
