package org.yxs.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.yxs.provider.common.entity.Demo;

import java.util.List;

/**
 * @author yxs
 */
@Mapper
public interface DemoMapper extends BaseMapper<Demo> {

    public List<Demo> getDemoList(IPage page);
}
