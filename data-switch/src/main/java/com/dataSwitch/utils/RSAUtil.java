package com.dataSwitch.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;


import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RSAUtil {


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

    // 用公钥加密
    public static String encrypt(String data, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    // 用私钥解密
    public static String decrypt(String data, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }


    /**
     *     // 用私钥签名
     * @param data
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String sign(String data, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     *     // 用公钥验证签名
     * @param data
     * @param signatureStr
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String signatureStr, String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getMimeDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(Base64.getDecoder().decode(signatureStr));
    }
}

