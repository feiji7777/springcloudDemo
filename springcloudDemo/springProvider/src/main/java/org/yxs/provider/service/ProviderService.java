package org.yxs.provider.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.yxs.provider.common.entity.Demo;

import java.util.Map;

public interface ProviderService {
    String send();

    String queryDemo();

    int delete();

    IPage<Demo> listPage(Map queryParam, Integer page, Integer size);
}
