package com.cz.controller;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-22
 */
import com.cz.util.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/systemb")
@RestController
public class MessageReceiverController {

    @Value("${key.publicKey}")
    private String PUBLIC_KEY_OF_A ;
    @Value("${key.privateKey}")
    private  String PRIVATE_KEY_OF_B ;

    @PostMapping("/receiveEncryptedMessage")
    public String receiveEncryptedMessage(@RequestBody Map<String, String> payload) throws Exception {
        String encryptedMessage = payload.get("encryptedMessage");
        String signature = payload.get("signature");

        // 验证签名
        boolean isVerified = RSAUtil.verify(encryptedMessage, signature, PUBLIC_KEY_OF_A);
        if (!isVerified) {
            return "Invalid Signature!";
        }

        // 解密消息
        String decryptedMessage = RSAUtil.decrypt(encryptedMessage, PRIVATE_KEY_OF_B);
        return "Decrypted Message: " + decryptedMessage;
    }
}
