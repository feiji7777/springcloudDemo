package com.example.demo.test1;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test2 {
    public static void main(String[] args) {
     /*   List<String> result = new ArrayList<>();
        result.add("1");
        result.add("2");
        result.add("3");

      String result1 =  String.join(",",  result);
      System.out.println(result1);*/

        String[] array = "1".split(",");
        List<String> cityIdList = Arrays.asList(array);
        System.out.println(cityIdList.size());
    }
}
