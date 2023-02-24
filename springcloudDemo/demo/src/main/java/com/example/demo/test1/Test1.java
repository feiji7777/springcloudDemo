package com.example.demo.test1;

import java.util.ArrayList;
import java.util.List;

public class Test1 {

    public static void main(String[] args) {
        Test1 test1 = new Test1();
        String result=test1.listWhere();
        System.out.println(result);
    }

    public String listWhere() {
        List<String> list = new ArrayList<>();
        list.add("c.is_delete = 0");

            list.add("c.id = #{id}");


            list.add("c.channel_name like concat('%',#{search},'%')");


            list.add("c.channel_no like concat('%',#{channelNo},'%')");


            list.add("c.status = #{status}");


            list.add("c.parent_id = #{parentId}");

//            if(null != query.getType()){
//                list.add("suc.type = #{type}");
//            }

//            if(null !=query.getUserId()){
//                list.add("suc.user_id = #{userId} ");
//            }
        return String.join(" and ", list);
    }
}
