package org.yxs.provider.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class ObjectConversion {

    /**
     * 从List<A> 转换 List<B>
     * @param list List<B>
     * @param clazz B
     * @return List<B>
     */
    public static <T> List<T> toList(List<?> list, Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(list), clazz);
    }

    /**
     * 从object 转换 List
     * @param list List<B>
     * @param clazz B
     * @return List<B>
     */
    public static <T> List<T> toList(Object obj, Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(obj), clazz) ;
    }

    /**
     * 从object对象 转换  对象B
     * @param ob A
     * @param clazz B.class
     * @return B
     */
    public static <T> T toObject(Object obj, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(obj), clazz);
    }
}
