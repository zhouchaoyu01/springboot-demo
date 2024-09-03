package com.dataSwitch.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * SM2Util 类用于生成、加密、解密、签名和验签 SM2 密钥对及数据。
 * 该类使用 BouncyCastle 提供的安全库来实现各种 SM2 操作。
 */
public class SM2Util {

    // 静态代码块，在类加载时自动添加 BouncyCastle 提供者
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 定义 SM2 曲线名称常量
    private static final String CURVE_NAME = "sm2p256v1";
    private static final SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;


    /**
     * 打印生成的 SM2 公钥和私钥，以十六进制格式显示
     */
    public static void printGenSM2Key() {
        // 获取 SM2 曲线参数
        X9ECParameters ecParameters = GMNamedCurves.getByName(CURVE_NAME);
        ECDomainParameters domainParameters = new ECDomainParameters(
                ecParameters.getCurve(),
                ecParameters.getG(),
                ecParameters.getN(),
                ecParameters.getH());

        // 使用 SM2 算法生成密钥对
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(domainParameters, new SecureRandom());
        keyPairGenerator.init(keyGenerationParameters);

        // 生成密钥对
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
        ECPoint publicKeyPoint = ((org.bouncycastle.crypto.params.ECPublicKeyParameters) keyPair.getPublic()).getQ();
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();

        // 获取公钥和私钥的字节数组
        byte[] publicKeyBytes = publicKeyPoint.getEncoded(false);  // false表示未压缩格式
        byte[] privateKeyBytes = privateKey.getD().toByteArray();

        // 将字节数组转换为 Hex String
        String publicKeyHex = Hex.toHexString(publicKeyBytes);
        String privateKeyHex = Hex.toHexString(privateKeyBytes);

        // 输出公钥和私钥的 Hex String
        System.out.println("Public Key (Hex): " + publicKeyHex);
        System.out.println("Private Key (Hex): " + privateKeyHex);
    }

    /**
     * 按规则拼接字符串
     */
    public static String jsonMapToStr(JSONObject jsonObject) {
        String[] keys = jsonObject.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder raw = new StringBuilder();
        for (String key : keys) {
            if (!StringUtils.isBlank(jsonObject.getString(key))) {
                raw.append(key).append("=").append(jsonObject.getString(key)).append("&");
            }
        }

        if (raw.length() > 0) {
            raw.deleteCharAt(raw.length() - 1);
        }
        return raw.toString();
    }

    /**
     * SM2 加密
     *
     * @param data 要加密的数据
     * @param publicKeyStr 公钥的字符串表示形式
     * @return 加密后的数据，以十六进制表示
     * @throws InvalidCipherTextException 加密失败
     */
    public static String encrypt(String data, String publicKeyStr) throws InvalidCipherTextException {
        byte[] dataBytes = data.getBytes();
        ECPublicKeyParameters publicKey = convertStringToPublicKey(publicKeyStr);
        // 使用 SM3 和 C1C3C2 模式
        SM2Engine engine = new SM2Engine(new SM3Digest(), mode);
        engine.init(true, new ParametersWithRandom(publicKey));
        byte[] bytes = engine.processBlock(dataBytes, 0, dataBytes.length);
        return Hex.toHexString(bytes);
    }

    /**
     * SM2 解密
     *
     * @param encryptedData 加密后的数据，以十六进制表示
     * @param privateKeyStr 私钥的字符串表示形式
     * @return 解密后的原始数据
     * @throws InvalidCipherTextException 解密失败
     */
    public static String decrypt(String encryptedData, String privateKeyStr) throws InvalidCipherTextException {
        byte[] dataBytes = Hex.decodeStrict(encryptedData);
        ECPrivateKeyParameters privateKey = convertStringToPrivateKey(privateKeyStr);
        // 使用 SM3 和 C1C3C2 模式
        SM2Engine engine = new SM2Engine(new SM3Digest(), mode);
        engine.init(false, privateKey);
        byte[] bytes = engine.processBlock(dataBytes, 0, dataBytes.length);
        return new String(bytes);
    }

    /**
     * 将字符串形式的公钥转换为 PublicKey 对象
     *
     * @param publicKeyStr 公钥的字符串表示形式
     * @return 转换后的 PublicKey 对象
     */
    public static ECPublicKeyParameters convertStringToPublicKey(String publicKeyStr) {
        // 获取 SM2 曲线参数
        X9ECParameters x9ECParameters = GMNamedCurves.getByName(CURVE_NAME);
        ECDomainParameters ecDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
        ECCurve curve = ecDomainParameters.getCurve();
        ECPoint ecPoint = curve.decodePoint(Hex.decode(publicKeyStr));
        return new ECPublicKeyParameters(ecPoint, ecDomainParameters);

    }

    /**
     * 将字符串形式的私钥转换为 PrivateKey 对象
     *
     * @param privateKeyStr 私钥的字符串表示形式
     * @return 转换后的 PrivateKey 对象
     */
    public static ECPrivateKeyParameters convertStringToPrivateKey(String privateKeyStr) {
        // 添加 BouncyCastle 作为安全提供者
        Security.addProvider(new BouncyCastleProvider());
        // 将 Hex 私钥转换为 BigInteger
        BigInteger privateKeyD = new BigInteger(1, Hex.decode(privateKeyStr));
        // 获取 SM2 曲线参数
        X9ECParameters x9ECParameters = GMNamedCurves.getByName(CURVE_NAME);
        ECDomainParameters ecDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
        return new ECPrivateKeyParameters(privateKeyD, ecDomainParameters);
    }

    /**
     * SM2 签名
     *
     * @param data 要签名的数据
     * @param privateKeyStr 私钥的字符串表示形式
     * @return 签名后的数据，以十六进制表示
     * @throws CryptoException 签名失败
     */
    public static String sign(String data, String privateKeyStr) throws CryptoException {
        byte[] dataBytes = data.getBytes();
        ECPrivateKeyParameters privateKey = convertStringToPrivateKey(privateKeyStr);

        // 创建 SM2Signer 对象
        SM2Signer signer = new SM2Signer();
        signer.init(true, new ParametersWithRandom(privateKey));
        // 更新签名器的数据
        signer.update(dataBytes, 0, dataBytes.length);
        // 生成签名
        byte[] bytes = signer.generateSignature();
        return Hex.toHexString(bytes);
    }

    /**
     * SM2 验签
     *
     * @param data 原始数据
     * @param signature 签名数据，以十六进制表示
     * @param publicKeyStr 公钥的字符串表示形式
     * @return 验签结果，true 表示签名有效，false 表示无效
     */
    public static boolean verify(String data, String signature, String publicKeyStr) {
        byte[] dataBytes = data.getBytes();
        byte[] signatureBytes = Hex.decodeStrict(signature);
        ECPublicKeyParameters publicKey = convertStringToPublicKey(publicKeyStr);

        // 创建 SM2Signer 对象
        SM2Signer signer = new SM2Signer();
        signer.init(false, publicKey);
        // 更新签名器的数据
        signer.update(dataBytes, 0, dataBytes.length);
        // 验证签名
        return signer.verifySignature(signatureBytes);
    }

    public static void main(String[] args) throws Exception {
        // 打印生成的 SM2 密钥对
//        printGenSM2Key();

        String data = "bizData={\"data\":[{\"distTbName\":\"student_bk\",\"endTime\":\"2024-08-29 16:30:10.000\",\"id\":\"1\",\"jsonObject\":{\"name|VARCHAR\":\"wwRVbu\",\"create_time|DATETIME\":\"2024-08-29 16:30:00\",\"id|INT\":\"4\",\"student_id|INT\":\"182037\",\"course_id|INT\":\"10054\",\"class_id|INT\":\"10142\"},\"orderId\":\"4\",\"srcTbName\":\"student_info\",\"startTime\":\"2024-08-29 16:30:00.000\"}]}&transCode=1&transDate=20240902&transTime=163934";
        String privateKey = "6db9e86a90d3e68900518cb3367b2758e943480b2d3474011b57d637787911f6";
        String publicKey = "04c236428f3ec9791c3a8986a9b53c200f5d7ebbf9298c2a7596370c6b8b10e70228268a2b5269549c0edfa3966d7f4f64441ef4f170baac866f8b4eb8cf362431";

//        String encryptData = encrypt(data, publicKey);
//        System.out.println("SM2 加密" + encryptData);
//        String decryptData = decrypt(encryptData, privateKey);
//        System.out.println("SM2 解密" + decryptData);

        String sign = sign(data, privateKey);
        System.out.println("SM2 加签" + sign);
        System.out.println("SM2 验签" + verify(data, sign, publicKey));
    }

}
