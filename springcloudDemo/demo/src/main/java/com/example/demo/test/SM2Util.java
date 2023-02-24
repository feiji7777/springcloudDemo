package com.example.demo.test;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * 1. @description: SM2工具类
 * 2. @author: xh
 * 3. @time: 2022/3/18
 */
@Slf4j
public class SM2Util {

    /**
     * 生成SM2公私钥对
     * @return
     */
    private static AsymmetricCipherKeyPair genKeyPair0() {
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");

        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        //1.创建密钥生成器
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        //2.初始化生成器,带上随机数
        try {
            keyPairGenerator.init(new ECKeyGenerationParameters(domainParameters, SecureRandom.getInstance("SHA1PRNG")));
        } catch (NoSuchAlgorithmException e) {
            log.error("生成公私钥对时出现异常:", e);
//            e.printStackTrace();
        }

        //3.生成密钥对
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        return asymmetricCipherKeyPair;
    }

    /**
     * 生成公私钥对(默认压缩公钥)
     * @return
     */
    public static SMKeyPair genKeyPair() {
        return genKeyPair(true);
    }

    /**
     * 生成公私钥对
     * @param compressedPubKey  是否压缩公钥
     * @return
     */
    public static SMKeyPair genKeyPair(boolean compressedPubKey) {
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = genKeyPair0();

        //提取公钥点
        ECPoint ecPoint = ((ECPublicKeyParameters) asymmetricCipherKeyPair.getPublic()).getQ();
        //公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
        String pubKey = Hex.toHexString(ecPoint.getEncoded(compressedPubKey));

        BigInteger privatekey = ((ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate()).getD();
        String priKey = privatekey.toString(16);

        SMKeyPair keyPair = new SMKeyPair(priKey, pubKey);
        return keyPair;
    }

    /**
     * 私钥签名
     * @param privateKey    私钥
     * @param content       待签名内容
     * @return
     */
    public static String sign(String privateKey, String content) throws CryptoException {
        //待签名内容转为字节数组
        byte[] message = Hex.decode(content);

        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        BigInteger privateKeyD = new BigInteger(privateKey, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

        //创建签名实例
        SM2Signer sm2Signer = new SM2Signer();

        //初始化签名实例,带上ID,国密的要求,ID默认值:1234567812345678
        try {
            sm2Signer.init(true, new ParametersWithID(new ParametersWithRandom(privateKeyParameters, SecureRandom.getInstance("SHA1PRNG")), Strings.toByteArray("1234567812345678")));
        } catch (NoSuchAlgorithmException e) {
            log.error("签名时出现异常:", e);
        }
        sm2Signer.update(message, 0, message.length);
        //生成签名,签名分为两部分r和s,分别对应索引0和1的数组
        byte[] signBytes = sm2Signer.generateSignature();

        String sign = Hex.toHexString(signBytes);

        return sign;
    }

    /**
     * 将R或者S修正为固定字节数
     * @param rs
     * @return
     */
    private static byte[] modifyRSFixedBytes(byte[] rs) {
        int length = rs.length;
        int fixedLength = 32;
        byte[] result = new byte[fixedLength];
        if (length < 32) {
            System.arraycopy(rs, 0, result, fixedLength - length, length);
        } else {
            System.arraycopy(rs, length - fixedLength, result, 0, fixedLength);
        }
        return result;
    }

    /**
     * 验证签名
     * @param publicKey     公钥
     * @param content       待签名内容
     * @param sign          签名值
     * @return
     */
    public static boolean verify(String publicKey, String content, String sign) {
        //待签名内容
        byte[] message = Hex.decode(content);
        byte[] signData = Hex.decode(sign);

        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        // 构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(),
                sm2ECParameters.getN());
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(publicKey));
        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
        //创建签名实例
        SM2Signer sm2Signer = new SM2Signer();
        ParametersWithID parametersWithID = new ParametersWithID(publicKeyParameters, Strings.toByteArray("1234567812345678"));
        sm2Signer.init(false, parametersWithID);
        sm2Signer.update(message, 0, message.length);

        boolean verify = sm2Signer.verifySignature(signData);
        return verify;
    }

    /**
     * SM2加密算法
     * @param publicKey     公钥
     * @param data          数据
     * @return
     */
    public static String encrypt(String publicKey, String data){
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        // 构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(),
                sm2ECParameters.getN());
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(publicKey));
        // 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));

        byte[] arrayOfBytes = null;
        try {
            byte[] in = data.getBytes("utf-8");
            arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
        } catch (Exception e) {
            log.error("SM2加密时出现异常:", e);
        }
        return Hex.toHexString(arrayOfBytes);
    }

    /**
     * SM2加密算法
     * @param publicKey     公钥
     * @param data          明文数据
     * @return
     */
    public static String encrypt(PublicKey publicKey, String data) {

        ECPublicKeyParameters ecPublicKeyParameters = null;
        if (publicKey instanceof BCECPublicKey) {
            BCECPublicKey bcecPublicKey = (BCECPublicKey) publicKey;
            ECParameterSpec ecParameterSpec = bcecPublicKey.getParameters();
            ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
                    ecParameterSpec.getG(), ecParameterSpec.getN());
            ecPublicKeyParameters = new ECPublicKeyParameters(bcecPublicKey.getQ(), ecDomainParameters);
        }

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));

        byte[] arrayOfBytes = null;
        try {
            byte[] in = data.getBytes("utf-8");
            arrayOfBytes = sm2Engine.processBlock(in,0, in.length);
        } catch (Exception e) {
            log.error("SM2加密时出现异常:", e);
        }
        return Hex.toHexString(arrayOfBytes);
    }

    /**
     * SM2解密算法
     * @param privateKey    私钥
     * @param cipherData    密文数据
     * @return
     */
    public static String decrypt(String privateKey, String cipherData) {
        byte[] cipherDataByte = Hex.decode(cipherData);

        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(),
                sm2ECParameters.getG(), sm2ECParameters.getN());

        BigInteger privateKeyD = new BigInteger(privateKey, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, privateKeyParameters);

        String result = null;
        try {
            byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
            return new String(arrayOfBytes, "utf-8");
        } catch (Exception e) {
            log.error("SM2解密时出现异常:", e);
        }
        return result;

    }

    /**
     * SM2解密算法
     * @param privateKey        私钥
     * @param cipherData        密文数据
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String cipherData) {
        byte[] cipherDataByte = Hex.decode(cipherData);

        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) privateKey;
        ECParameterSpec ecParameterSpec = bcecPrivateKey.getParameters();

        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(),
                ecParameterSpec.getG(), ecParameterSpec.getN());

        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bcecPrivateKey.getD(),
                ecDomainParameters);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, ecPrivateKeyParameters);

        String result = null;
        try {
            byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
            return new String(arrayOfBytes, "utf-8");
        } catch (Exception e) {
            log.error("SM2解密时出现异常:", e);
        }
        return result;
    }

    /**
     * 将未压缩公钥压缩成压缩公钥
     * 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
     * @param pubKey    未压缩公钥(16进制,不要带头部04)
     * @return
     */
    public static String compressPubKey(String pubKey) {
        pubKey = "04" + pubKey;    //将未压缩公钥加上未压缩标识.
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(pubKey));
        String compressPubKey = Hex.toHexString(pukPoint.getEncoded(Boolean.TRUE));

        return compressPubKey;
    }

    /**
     * 将压缩的公钥解压为非压缩公钥
     * @param compressKey   压缩公钥
     * @return
     */
    public static String unCompressPubKey(String compressKey) {
        // 获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //提取公钥点
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(compressKey));
        String pubKey = Hex.toHexString(pukPoint.getEncoded(Boolean.FALSE));
        //去掉前面的04   (04的时候，可以去掉前面的04)
        pubKey = pubKey.substring(2);
        return pubKey;
    }



    public static void main(String[] args) {
        SMKeyPair smKeyPair = genKeyPair(false);
        String priKey = smKeyPair.getPriKey();
        System.out.println("私钥"+ priKey);
        String pubKey = "MIICyjCCAm2gAwIBAgIFQEFXJEYwDAYIKoEcz1UBg3UFADBhMQswCQYDVQQGEwJDTjEwMC4GA1UECgwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MSAwHgYDVQQDDBdDRkNBIEFDUyBURVNUIFNNMiBPQ0EzMTAeFw0yMjEwMjYwODAwMDdaFw0yNTEwMjYwODAwMDdaMHUxCzAJBgNVBAYTAkNOMRgwFgYDVQQKDA9DRkNBIFRFU1QgT0NBMzExETAPBgNVBAsMCExvY2FsIFJBMRkwFwYDVQQLDBBPcmdhbml6YXRpb25hbC0yMR4wHAYDVQQDDBUwNTFAVGVzdEBOMTIzNDU2NzhAMjAwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAS8nvWAETVfX0Ev+dKA6sPn1ssgRnulDdeGULA5apV2chhIEAzr7MsL9R6ha8ZxlmHKdN4nO+LbaXvxGQ5vdG/No4H7MIH4MD8GCCsGAQUFBwEBBDMwMTAvBggrBgEFBQcwAYYjaHR0cDovL29jc3B0ZXN0LmNmY2EuY29tLmNuOjgwL29jc3AwHwYDVR0jBBgwFoAUBMe8+VkBaT6MNDYgYhg83ry1uwwwDAYDVR0TAQH/BAIwADA4BgNVHR8EMTAvMC2gK6AphidodHRwOi8vMjEwLjc0LjQyLjMvT0NBMzEvU00yL2NybDE5My5jcmwwDgYDVR0PAQH/BAQDAgM4MB0GA1UdDgQWBBSrAkejzFMdY+JqK/Mpf+iTAFANJjAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwDAYIKoEcz1UBg3UFAANJADBGAiEAzxN0lArq/O0fVfFSXz9x1LFV3JQ5aCOWcL9wL2RKSxwCIQDc1bc29Ty5tzeOkikDyRc9Bq9Z6pbkMcy2P4NmpVVQjA==";
        System.out.println("公钥"+ pubKey);
        //公钥解压缩
        String substring = pubKey.substring(2, pubKey.length());
        String s = compressPubKey(substring);
        System.out.println("压缩后" + s);
        String s1 = unCompressPubKey(s);
        System.out.println("解压后" + s1);
        //明文
        String text = "123123123";
        System.out.println("测试明文文本" + text);
        //签名验签测试
        String sign = "";
        try {
            sign = sign(priKey, Hex.toHexString(text.getBytes()));
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        System.out.println("生成签名" + sign);
        boolean verify = verify(pubKey, Hex.toHexString(text.getBytes()), sign);
       // System.out.println("验签结果" + verify);


        //加解密测试
        String encryptData = encrypt(pubKey, text);
        System.out.println("加密结果" + encryptData);
        encryptData="b3a780dc34a03d39b18578310caa6717fbcd790790f3a181e39944ba05e84af669f8ed7476745805d9a68decf83cda6d85dfd9198c44d04b9916007c8fcbf3f10f5dc71f8149ad19bc461fa66c60a414d99be5b66e0db71766d57c90debfae9f681d2374e48e7e3bd24ab0de4d3aa0b8275cc3eaf0870cb97601b047017077739be8a71cfe2315a05cf12996a023cb0f3f4da1cf863f05cc8841a965ec40220b86477839461edb394b667b2d261a4faa2809bc42cdda7de4e5674dc4a1ff06dc";
        String decryptData = decrypt(priKey, encryptData);
        System.out.println("解密结果" + decryptData);
    }

}


