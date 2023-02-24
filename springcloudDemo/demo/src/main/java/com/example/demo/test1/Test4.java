package com.example.demo.test1;

import java.util.Arrays;

public class Test4 {

    public static void main(String[] args) {
        Integer[] array={2,5,6,1,9};

        for(int i=0;i<array.length-1;i++){

            for(int j=0;j<array.length-1-i;j++){
                int num=0;
                if(array[j+1]>array[j]){
                    num = array[j];
                    array[j]=array[j+1];
                    array[j+1]=num;
                }
            }

        }
        System.out.println(Arrays.asList(array));
    }


}
