package org.yxs.provider.consumer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yxs.provider.common.entity.Demo;
import org.yxs.provider.service.ProviderService;

import java.util.HashMap;

@RestController
@RequestMapping("provider1")
public class ProviderTestController {
    @Autowired
    private ProviderService providerService;
    @GetMapping("/test")
    public IPage<Demo> pageDemo() {
        IPage<Demo>  pages = providerService.listPage(new HashMap(),2,3);
        return pages;
    }
}
