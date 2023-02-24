package org.yxs.fallback;

import org.springframework.stereotype.Component;
import org.yxs.client.ProviderFeignClient;

@Component
public class DefaultFallback implements ProviderFeignClient {
    @Override
    public String send() {
        return "发送消息出错！";
    }

    @Override
    public String receive(String msg) {
        return "收到消息出错！";
    }
}
