package org.yxs.consumer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yxs.client.ProviderFeignClient;
import org.yxs.consumer.service.ConsumerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private ProviderFeignClient providerFeignClient;

    @Override
    public String send() {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            String loadBalancerResponse = providerFeignClient.receive("由consumer发送请求到provider");
            return loadBalancerResponse;
        });
        String loadBalancerResponse = null;
        try {
            loadBalancerResponse = f.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (loadBalancerResponse == null) {
            return null;
        }


        return loadBalancerResponse;
        //return "";
    }
}
