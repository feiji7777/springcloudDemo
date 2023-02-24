package com.example.demo.test1;

public class SimpleFormat {

    // 使用1个字节表示
    public static String numToHex8(int b) {
        return String.format("%02x", b);// 2表示需要两个16进制数
    }

    // 使用2个字节表示
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    public static String numToHex161(int b) {
        return String.format("%04d", b);
    }


    /**
     * 将10进制整型转为16进制字符串 （使用4个字节表示）
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex32(long b) {
        return String.format("%08x", b);
    }

    /**
     * 使用8个字节表示
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex64(long b) {
        return String.format("%016x", b);
    }

    /**
     * 获取16进制的底图标签值
     * @param  10进制底图标签值
     * @return 16进制底图标签值
     */
    public static String hexString(long b) {
        return "0x".concat(numToHex32(b));
    }

    public static void main(String[] args) {
        System.out.println(numToHex161(1));
    }
}
