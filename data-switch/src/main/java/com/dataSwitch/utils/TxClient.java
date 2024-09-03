package com.dataSwitch.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dataSwitch.config.BankConfig;
import com.dataSwitch.http.BizParameter;
import com.dataSwitch.http.TxRequest;
import com.dataSwitch.http.TxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TxClient {
    private static final String YYYY_MM_DD = "yyyyMMdd";
    private static final String HH_MM_SS = "HHmmss";

    private static final String HTTP_RSP_ERROR = "E10001";
    private final Logger logger = LoggerFactory.getLogger(TxClient.class);
    private String appId;

    @Autowired
    private BankConfig config;

    @Autowired
    private RestTemplate restTemplate;

    public TxClient(BankConfig config) {
        this.config = config;
    }
    private TxRequest assembleRequest(String transCode, BizParameter param) throws Exception {
        TxRequest request = new TxRequest();
        request.setAppId(this.config.getAppId());//todo

        request.setTransCode(transCode);
//        request.setFormat(this.config.getFormat());
//        request.setCharset(this.config.getCharset());
        request.setTransDate(new SimpleDateFormat(YYYY_MM_DD).format(new Date()));
        request.setTransTime(new SimpleDateFormat(HH_MM_SS).format(new Date()));
//        request.setVersion(this.config.getVersion());
        request.setBizData(param.toString());
        String signedValue = SM2Util.jsonMapToStr((JSONObject) JSON.toJSON(request));
        logger.info("待签名源串:{}", signedValue);

        // 对消息进行加密
//        String encryptedMessage = SM2Util.encrypt(signedValue, this.config.getPublicKey());
        // 对消息进行签名
        String signature = SM2Util.sign(signedValue, this.config.getPrivateKey());

        request.setSignType(this.config.getSignType());
        request.setSign(signature);
        return request;
    }
    private void verify(String respStr) throws Exception {
        JSONObject map = JSON.parseObject(respStr);
        String sign = (String) map.remove("sign");
        String signType = (String) map.remove("signType");
        String srcSignMsg = SM2Util.jsonMapToStr(map);
        if (!SM2Util.verify(srcSignMsg, sign, this.config.getExtPublicKey())) {
            throw new Exception("响应报文验签失败");
        }
    }


    public TxResponse sendRequest(String transCode, BizParameter param, String url) throws Exception {
        TxRequest request = assembleRequest(transCode, param);
        logger.info("request:{}", request);
        String respStr = post(request.toString(), url);
        logger.info("response:{}", respStr);
        verify(respStr);
        logger.info("验签成功");
        return JSON.parseObject(respStr, TxResponse.class);
    }

    private String post(String data, String url){
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 发送http服务请求
        ResponseEntity<String> response = restTemplate.postForEntity(url, data, String.class);
        // 判断返回结果
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }else {
            logger.info("请求失败 HTTP状态码:{} 请求地址:{}",response.getStatusCode(), url);
            TxResponse txResponse = new TxResponse();
            txResponse.setCode(HTTP_RSP_ERROR);
            txResponse.setMsg("系统请求失败");
            return JSONObject.toJSONString(txResponse);
        }
    }


}
