package com.example.demo.test1;

public class Test3 {
    public static void main(String[] args) {
        String macNo = "102C6BC5649D";
        macNo = Test3.toMacNo(macNo);
        System.out.println(macNo.substring(0,2));

        System.out.println("01".substring(0,2));


    }


    public static String toMacNo(String macNo){
        StringBuilder tmp = new StringBuilder();
        char ch;
        for(int i = 0; i< macNo.length(); i++){
            ch = macNo.charAt(i);

            if(i % 2 == 0 && i != 0){
                tmp.append(':');
            }
            tmp.append(ch);
        }
        return tmp.toString();
    }
}
