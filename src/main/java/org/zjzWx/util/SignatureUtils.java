package org.zjzWx.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * HMAC签名工具类
 * 提供签名生成、验证和十六进制转换功能
 */
public class SignatureUtils {

    /**
     * 生成HMAC-SHA256签名
     */
    public static String generateSignature(
            String apiSecret,
            String method,
            String path,
            String timestamp,
            String nonce,
            Map<String, String> params,
            String body) throws NoSuchAlgorithmException, InvalidKeyException {

        String signData = buildSignData(
                method,
                path,
                timestamp,
                nonce,
                params,
                body
        );

        // 使用API密钥生成签名
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(
                apiSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        hmac.init(key);
        byte[] hash = hmac.doFinal(signData.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * 构建签名字符串
     */
    private static String buildSignData(
            String method,
            String path,
            String timestamp,
            String nonce,
            Map<String, String> params,
            String body) {

        StringBuilder sb = new StringBuilder();
        sb.append(method.toUpperCase()).append("\n")
                .append(path).append("\n")
                .append(timestamp).append("\n")
                .append(nonce).append("\n");

        // 处理排序后的参数
        if (params != null && !params.isEmpty()) {
            Map<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
            sb.deleteCharAt(sb.length() - 1).append("\n");
        }

        // 处理请求体
        if (body != null && !body.isEmpty()) {
            sb.append(body).append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}