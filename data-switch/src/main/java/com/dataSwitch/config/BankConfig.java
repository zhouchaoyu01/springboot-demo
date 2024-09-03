package com.dataSwitch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties(prefix = "bank")
public class BankConfig {


    private String url;//请求地址
    private String appId;
    private String extPublicKey;//外部系统rsa公钥
    private String publicKey ;//本系统rsa公钥
    private String privateKey; //本系统rsa私钥
    private String format = "json";
    private String charset = "UTF-8";
    private String signType = "RSA";
    private String version = "1.0";
    private String notifyUrl;
    private String respUrl;

    public BankConfig() {

    }
}