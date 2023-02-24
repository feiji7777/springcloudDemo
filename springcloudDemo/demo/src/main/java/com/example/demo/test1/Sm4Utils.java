package com.example.demo.test1;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

/**
 * @explain sm4加密、解密与加密结果验证可逆算法
 * @author lenny
 * @creationTime 20210412
 * @version 1.0
 */
public class Sm4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";

    /**
     * 加密算法/分组加密模式/分组填充方式
     * 以8个字节为一组进行分组加密
     * 分组填充方式使用：分组填充方式有多种，包含PKCS5Padding、PKCS7Padding、ZeroBytePadding等等,我们的产品使用的是ZeroBytePadding
     */
    private static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/ZeroBytePadding";

    /**
     * 密钥掩码本
     */
    private static final byte[] CODEBOOK={ 0x36,0x18,0x02, (byte) 0xA5, (byte) 0xB6, (byte) 0xCC, (byte) 0x88,0x66, (byte) 0xF9, (byte) 0xE5, (byte) 0xD3, (byte) 0x96,0x23,0x45, (byte) 0x89,0x36};

    /**
     * 获取密钥
     * @param strKey
     * @return
     */
    public static byte[] getSecretKey(String strKey)
    {
        byte[] bytes=new byte[CODEBOOK.length];
        int sum = checkSum(strKey);
        for(int i=0;i<CODEBOOK.length;i++)
        {
            bytes[i] = (byte)(sum^CODEBOOK[i]);
        }
        return bytes;
    }

    /**
     * 对Key值进行按字节求和
     * @param strKey
     * @return
     */
    private static int checkSum(String strKey)
    {
        byte[] keyBytes=strKey.getBytes();
        int sum = 0;
        for(byte item : keyBytes) {
            sum += item;
        }
        return sum & 0xff;
    }
    /**
     * 生成ECB暗号
     * @explain ECB模式（电子密码本模式：Electronic codebook）
     * @param algorithmName 算法名称
     * @param mode 模式
     * @param key
     * @return
     * @throws Exception
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    /**
     * sm4加密
     * @explain 加密模式：ECB 密文长度不固定，会随着被加密字符串长度的变化而变化
     * @param hexKey 16进制密钥（忽略大小写）
     * @param paramStr  待加密字符串
     * @return 返回16进制的加密字符串
     * @throws Exception
     */
    public static String encryptEcb(String hexKey, String paramStr,boolean isHex) throws Exception {
        String cipherText = "";
// 16进制字符串-->byte[]
        byte[] keyData = ByteUtils.fromHexString(hexKey);
// String-->byte[]
        if(!isHex) {
            byte[] srcData = paramStr.getBytes(ENCODING);
// 加密后的数组
            byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
// byte[]-->hexString
            cipherText = ByteUtils.toHexString(cipherArray);
        }else
        {
            byte[] srcData = ByteUtils.fromHexString(paramStr);
// 加密后的数组
            byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
// byte[]-->hexString
            cipherText = ByteUtils.toHexString(cipherArray);
        }
        return cipherText;
    }


    /**
     * 加密模式之Ecb
     * @explain
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * sm4解密
     * @explain 解密模式：采用ECB
     * @param hexKey 16进制密钥
     * @param cipherText 16进制的加密字符串（忽略大小写）
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decryptEcb(String hexKey, String cipherText) throws Exception {
// 用于接收解密后的字符串
        String decryptStr = "";
// hexString-->byte[]
        byte[] keyData = ByteUtils.fromHexString(hexKey);
// hexString-->byte[]
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
// 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
// byte[]-->String
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }


    /**
     * sm4解密
     * @param keyData 密钥
     * @param cipherData 加密消息体
     * @return 解密后的消息体
     * @throws Exception
     */
    public static byte[] decryptEcb(byte[] keyData, byte[] cipherData) throws Exception {
// 解密
        return decrypt_Ecb_Padding(keyData, cipherData);
    }

    /**
     * sm4解密
     * @param key 密钥关键字
     * @param cipherData
     * @return
     * @throws Exception
     */
    public static byte[] decryptEcb(String key, byte[] cipherData) throws Exception {
        try {
//得到密钥
            byte[] keyData = getSecretKey(key);
// 解密
            return decrypt_Ecb_Padding(keyData, cipherData);
        }catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 解密
     * @explain
     * @param key
     * @param cipherText
     * @return
     * @throws Exception
     */
    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    /**
     * @explain 校验加密前后的字符串是否为同一数据
     * @param hexKey 16进制密钥（忽略大小写）
     * @param cipherText 16进制加密后的字符串
     * @param paramStr 加密前的字符串
     * @return 是否为同一数据
     * @throws Exception
     */
    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
// 用于接收校验结果
        boolean flag = false;
// hexString-->byte[]
        byte[] keyData = ByteUtils.fromHexString(hexKey);
// 将16进制字符串转换成数组
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
// 解密
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
// 将原字符串转换成byte[]
        byte[] srcData = paramStr.getBytes(ENCODING);
// 判断2个数组是否一致
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    /**
     * 将byte[]转成16进制字符串
     * @param b
     * @return
     */
    private static String conver16HexStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            if ((b[i] & 0xff) < 0x10) {
                result.append("0");
            }
            result.append(Long.toString(b[i] & 0xff, 16));
        }
        return result.toString().toUpperCase();
    }

    /**
     * 不足真实的消息体长度，则末尾补充0x00
     * @param data1 解密后的数据
     * @param length 需要补充的消息体长度
     * @return
     */
    public static byte[] addBytes(byte[] data1, int length) {
        byte[] data2=new byte[length];
        for (int i=0;i<length;i++)
        {
            data2[i]=0x00;
        }
        byte[] data3 = new byte[data1.length + length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, length);
        return data3;
    }

//    public static void main(String [] args) throws Exception {
//        String key=conver16HexStr(getSecretKey("704000000004"));
//        System.out.println(key);
//System.out.println(new String(decryptEcb(ByteUtils.fromHexString(key)
          //      ,ByteUtils.fromHexString("cff61cc07e4f7a473a3f56033586643fc1e43d7bcf46d5e86b40cb2b5e90a3f8"))));
//        String rowData="00000000000000000000000000000000000000000000200401202849300116310100D40101D5020170F8020113F9020006FA060000FFFF0007FB144A6F696E74656368000000000000000000000000FD0901CC000D21A7420000";
//        System.out.println("原始的数据："+rowData);
//        String str=encryptEcb(key,rowData,true);
//        System.out.println("加密后数据："+str.toUpperCase());
//System.out.println(ByteBufUtil.hexDump(addBytes(decryptEcb(ByteUtils.fromHexString("6B455FF8EB91D53BA4B88ECB7E18D46B")
          //      ,ByteUtils.fromHexString(str)),2)));
//
//        System.out.println("解密后数据："+ByteBufUtil.hexDump(decryptEcb(ByteUtils.fromHexString(key)
//                ,ByteUtils.fromHexString(str))));
//    }
}
