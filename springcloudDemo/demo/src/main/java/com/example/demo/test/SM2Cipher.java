package com.example.demo.test;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class SM2Cipher {
    public static String CURVE_NAME = "sm2p256v1";

    /**
     * 获取 SM2 的曲线参数
     *
     * @return SM2 的曲线参数
     */
    private static ECDomainParameters getECDomainParameters() {
        ECParameterSpec spec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        return new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
    }

    /**
     * 获取 SM2 曲线
     *
     * @return SM2 曲线
     */
    private static ECCurve getSM2Curve() {
        ECParameterSpec spec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        return spec.getCurve();
    }

    /**
     * 将公钥字节数组编码为公钥对象
     *
     * @param value 公钥字节数组，65 字节，04||X||Y
     * @return 公钥对象
     */
    private static ECPublicKeyParameters encodePublicKey(byte[] value) {
        byte[] x = new byte[32];
        byte[] y = new byte[32];
        System.arraycopy(value, 1, x, 0, 32);
        System.arraycopy(value, 32 + 1, y, 0, 32);
        BigInteger X = new BigInteger(1, x);
        BigInteger Y = new BigInteger(1, y);
        ECPoint Q = getSM2Curve().createPoint(X, Y);
        return new ECPublicKeyParameters(Q, getECDomainParameters());
    }

    /**
     * 将私钥字节数组编码为私钥对象
     *
     * @param value 私钥字节数组，32 字节
     * @return 私钥对象
     */
    private static ECPrivateKeyParameters encodePrivateKey(byte[] value) {
        BigInteger d = new BigInteger(1, value);
        return new ECPrivateKeyParameters(d, getECDomainParameters());
    }


    /**
     * 将 SM2 的密文格式由 C1C2C3 转换为 C1C3C2
     *
     * @param cipherText C1C2C3 格式的密文
     * @return C1C3C2 格式的密文
     */
    private static byte[] C1C2C3ToC1C3C2(byte[] cipherText) {
        byte[] bytes = new byte[cipherText.length];
        System.arraycopy(cipherText, 0, bytes, 0, 64);
        System.arraycopy(cipherText, cipherText.length - 32, bytes, 64, 32);
        System.arraycopy(cipherText, 64, bytes, 96, cipherText.length - 96);
        return bytes;
    }

    /**
     * 将 SM2 的密文格式由 C1C3C2 转换为 C1C2C3
     *
     * @param cipherText C1C3C2 格式的密文
     * @return C1C2C3 格式的密文
     */
    private static byte[] C1C3C2ToC1C2C3(byte[] cipherText)  {
        byte[] bytes = new byte[cipherText.length];
        System.arraycopy(cipherText, 0, bytes, 0, 64);
        System.arraycopy(cipherText, 96, bytes, 64, cipherText.length - 96);
        System.arraycopy(cipherText, 64, bytes, cipherText.length - 32, 32);
        return bytes;
    }

    public static byte[] decrypt(String privateKey, byte[] message) throws InvalidCipherTextException {
        ECPrivateKeyParameters vk = encodePrivateKey(Hex.decode(privateKey));
        SM2Engine engine = new SM2Engine();
        engine.init(false, vk);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(0x04);
        stream.write(C1C3C2ToC1C2C3(message), 0, message.length);
        return engine.processBlock(stream.toByteArray(), 0, message.length + 1);
    }

    public static byte[] encrypt(String pk, byte[] message) throws InvalidCipherTextException, NoSuchAlgorithmException {
        ECPublicKeyParameters publicKey = encodePublicKey(Hex.decode("04" + pk));
        ParametersWithRandom parameters = new ParametersWithRandom(publicKey, SecureRandom.getInstanceStrong());
        SM2Engine engine = new SM2Engine();
        engine.init(true, parameters);
        byte[] bytes = engine.processBlock(message, 0, message.length);
        return C1C2C3ToC1C3C2(Arrays.copyOfRange(bytes, 1, bytes.length));
    }

    public static void main(String[] args) throws Exception {
       // String privateKey = "ae7a37ea74631d9f4a2f95fe23aeb9f4b802229b24ff77bf16a9deb25ca7fab7";

        String privateKey="8d5380c32a55f825d23f8416f0574e1b";
        String publicKey = "a7704c7663f8521b11b015085e3ea047ebd38372e6d14e27951aab5b8e7d4043f28f70888e2d5b64058a56ccf07e0a6acf47d591f2a007c6581087f68d985d11";

        byte[] ciphertext = encrypt(publicKey, "02|1111111111111111".getBytes());
        System.out.println("加密: " + Hex.toHexString(ciphertext).toUpperCase());

        String ss="MH0CIQD3I9E00Is7zEzkJHi66hBrcIg1QuL5KlvP+/D0jOYbZAIhAK9bUUs3tA5zbZYzlCPWPlUZQgoKMDOtLRXqq0IeNPQJBCCUdq0iUZpSoxUvwwCMWjHvnFyAJqDeQbb/nK4+VMv2oAQT8S7Gypxjs2453gSfv48mcNUHig==";
        byte[] result = decrypt(privateKey, Base64.decode(ss.getBytes()));
        System.out.println("解密: " + new String(result));

        String cerPath="/Users/yxs/Downloads/Z2025811000014_4041572446_SM2_EncCert.cer";//Z2025811000014_4041555276_SM2_EncCert
        Security.addProvider(new BouncyCastleProvider());
        InputStream fileInputStream = new FileInputStream(new File(cerPath));
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
        System.out.println("证书公钥："+ x509Certificate.getPublicKey());
        System.out.println(Hex.toHexString(x509Certificate.getPublicKey().getEncoded()));

        BigInteger d = new BigInteger("2417735637339060993855775620398518531566329782336108185649922691391470414561975431234418692568420686965334875909226616773415407980214112732101039644107972761309046832600181560070741395510621332615673787602426110107260579256682765491766809959952003161028867614933951753494327847871389481347500958202263176972108317320631125988823693969364082429369900284287102531918366751932835204221918628017463707209328505298029007957994068204972863481527150545557825069641165616340502055567722255184150151428621941348746183400510745459760009510370070236372048747151030218361731491175777024631987831243903777908098977729883256684763159413621090560952594078701229567090083875350211317338284667884662848713094003424045107568652080345508227682626398715379297798067816503378320782927335745554179045606641368221068801723964451116935646206950693539076300062334070267470636");
       //BCECPrivateKey bcecPrivateKey = getPrivatekeyFromD(d);

    }




}