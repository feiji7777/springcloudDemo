package org.yxs.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yxs.client.ConsumerFeignClient;
import org.yxs.provider.common.entity.Demo;
import org.yxs.provider.mapper.DemoMapper;
import org.yxs.provider.service.ProviderService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProviderServiceImpl implements ProviderService {
    @Autowired
    private ConsumerFeignClient consumerFeignClient;

    @Autowired
    private DemoMapper demoMapper;

    @Override
    public String send() {
     // List<Demo> demoList = demoMapper.getDemoList();
      //  System.out.println(JSON.toJSONString(demoList));

        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            String loadBalancerResponse = consumerFeignClient.receive("由consumer向provider发送请求！");
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
        //return consumerFeignClient.receive("由consumer向provider发送请求！");
    }

    @Override
    public String queryDemo() {
      Demo demo =  demoMapper.selectOne(new QueryWrapper<Demo>().eq("name","小白兔"));
        return JSON.toJSONString(demo);
    }

    @Override
    public int delete() {
        Demo demo = new Demo();
        demo.setDeletedFlag(1);
        UpdateWrapper<Demo> wrapper = new UpdateWrapper<Demo>();
        wrapper.eq("id", "eaa6c1116b41dc10a94eae34cf990133");
        wrapper.eq("deleted_flag", 0);
       int result = demoMapper.update(demo, wrapper);
        return result;
    }

    @Override
    public IPage<Demo> listPage(Map queryParam, Integer page, Integer size) {
        LambdaQueryWrapper<Demo> queryWrapper = Wrappers.lambdaQuery();
        //根据queryParam设置Wrapper的查询条件
        //比如:queryWrapper.eq(TStation::getFrameNo,queryParam.get("frameName"));
       // queryWrapper.eq(Demo::getName,"");
        return page(new Page<Demo>(page,size), queryWrapper);
    }

    private IPage<Demo> page(Page<Demo> objectPage, LambdaQueryWrapper<Demo> queryWrapper) {
        IPage<Demo> iPage = objectPage;
        List<Demo> demoList  = demoMapper.getDemoList(iPage);
        System.out.println("list.size=" + demoList.size());
        System.out.println("page.total=" + iPage.getTotal());
        IPage<Demo> result =demoMapper.selectPage(iPage,queryWrapper);
        iPage.setRecords(demoList);
        return result;
    }
}
