package org.zjzWx.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.zjzWx.service.UploadService;
import org.zjzWx.util.HttpUtil;
import org.zjzWx.util.PicUtil;
import org.zjzWx.util.R;


import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService {


    @Override
    public String checkNsfw(MultipartFile multipartFile) {


        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file",new PicUtil.MultipartInputStreamFileResource(multipartFile));

            ResponseEntity<String> response = HttpUtil.post(body, "checkNsfw");
            R r = JSON.parseObject(response.getBody(), R.class);
            if (r != null && !r.getCode().equals(200)) {
                log.error("调用鉴黄API发生错误:{}",r.getMsg());
                return null;
            }
            // 解析JSON获取鉴黄结果
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String code = jsonNode.get("code").asText();
            if(code.equals("0")){
                return null;
            }else {
                return jsonNode.get("msg").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "系统繁忙，请稍后再试";
        }
    }





    @Override
    public R uploadPhoto(MultipartFile file, String originalFilename) {
        try {

            // 直接获取文件内容
            byte[] fileContent = file.getBytes();

            // 进行Base64编码
            String base64Image = Base64.getEncoder().encodeToString(fileContent);

            // 拼接完整的Base64图片URI
            String imagePrefix = "";
            if (originalFilename.toLowerCase().endsWith(".png")) {
                imagePrefix = "data:image/png;base64,";
            } else if (originalFilename.toLowerCase().endsWith(".jpg") || originalFilename.toLowerCase().endsWith(".jpeg")) {
                imagePrefix = "data:image/jpeg;base64,";
            }

            return R.ok(imagePrefix + base64Image);

        } catch (IOException e) {
            return R.no("图片识别失败，请重试");
        }
    }




}
