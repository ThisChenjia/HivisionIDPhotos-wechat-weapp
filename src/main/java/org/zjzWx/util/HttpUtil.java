package org.zjzWx.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class HttpUtil {

    @Value("${webset.apiKey}")
    private String apiKey;
    @Value("${webset.apiSecret}")
    private String apiSecret;
    @Value("${webset.apiDomain}")
    private String apiDomain;

    private static final String METHOD = "POST";

    private static String staticApiKey;
    private static String staticApiSecret;
    private static String staticApiDomain;

    @PostConstruct
    public void init() {
        staticApiKey = this.apiKey;
        staticApiSecret = this.apiSecret;
        staticApiDomain = this.apiDomain;
    }

    public static ResponseEntity<String> post(MultiValueMap<String, Object> requestBody,String type){
        HttpHeaders headers = new HttpHeaders();
        try {
            String nonce = generateNonceByUUID();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String sign = SignatureUtils.generateSignature(staticApiSecret, METHOD, type, timestamp, nonce, null, JSON.toJSONString(requestBody));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Api-Key",staticApiKey);
            headers.set("X-Timestamp", timestamp);
            headers.set("X-Nonce", nonce);
            headers.set("X-Signature", sign);
        }catch (Exception e){
            log.error("设置请求头异常：{}",e.getMessage());
            throw new RuntimeException("设置请求头异常!");
        }

        Map<String,Object> params = new HashMap<>();
        params.put("type",type);
        params.put("requestBody",JSON.toJSONString(requestBody));
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                staticApiDomain,
                HttpMethod.POST,
                requestEntity,
                String.class);
    }


    public static String generateNonceByUUID() {
        // 生成标准UUID并移除横杠
        return UUID.randomUUID().toString().replace("-", "");
    }

}
