package com.cz.controller;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-22
 */
import com.cz.util.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/systema")
public class MessageController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${system.b.url}")
    private String systemBUrl;

    @Value("${key.publicKey}")
    private  String PUBLIC_KEY_OF_B ;
    @Value("${key.privateKey}")
    private  String PRIVATE_KEY_OF_A ;

    @PostMapping("/sendEncryptedMessage")
    public String sendEncryptedMessage(@RequestBody String message) throws Exception {
        // 对消息进行加密
        String encryptedMessage = RSAUtil.encrypt(message, PUBLIC_KEY_OF_B);

        // 对消息进行签名
        String signature = RSAUtil.sign(encryptedMessage, PRIVATE_KEY_OF_A);

        // 准备发送的数据
        Map<String, String> payload = new HashMap<>();
        payload.put("encryptedMessage", encryptedMessage);
        payload.put("signature", signature);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        // 发送请求到系统B
        String response = restTemplate.postForObject(systemBUrl + "/receiveEncryptedMessage", request, String.class);

        return "Response from System B: " + response;
    }
}
