package org.yxs;

import java.util.Arrays;
import java.util.Date;

/**
 * @author zj
 * @date 2023/2/23 0023 - 16:01
 */
public class TEST1 {

    public static final String B = "123123";
            public static final int AAA = 1;
        short a= 1;

    public static void main(String[] args) {
        System.out.println("hello");
        Date date = new Date();
        extracted();
        
        // try catch 快捷键 alt + shift + z

        // 转换为 大写 ctrl + shift + y

        // 全局搜索 ctrl+ h

        //查找文件 double shift

        // 选中部分代码 自动生成一个方法 ctrl + alt + m

        // 打开最近修改的文件 ctrl + e

        System.out.println();
        System.out.println("TEST1.main");
        System.out.println("args = " + Arrays.toString(args));
        System.out.println("date = " + date);



        for (String arg : args) {

        }

        if (date == null) {

        }

        if (date != null) {

        }

    }
    
    private static void extracted() {
        try {
            String aaa = "123";
            aaa = "3333";

            if (aaa != null) {

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
