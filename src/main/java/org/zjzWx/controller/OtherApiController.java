package org.zjzWx.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zjzWx.model.dto.ExploreDto;
import org.zjzWx.service.OtherApiService;
import org.zjzWx.util.R;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@RequestMapping("/otherApi")
public class OtherApiController {

    @Autowired
    private OtherApiService otherApiService;


    @GetMapping("/exploreCount")
    public R exploreCount(){
        return R.ok(otherApiService.exploreDtoCount());
    }

    @GetMapping("/checkTheFreeQuota")
    public R checkTheFreeQuota(Integer type,Integer type2) {
        long count = otherApiService.checkTheFreeQuota(type, type2, Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        return R.ok(count);
    }


    @PostMapping("/colourize")
    public R colourize(@RequestBody ExploreDto exploreDto) {
        exploreDto.setUserId(Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        String colourize = otherApiService.colourize(exploreDto);
        if(null==colourize){
            return R.no("图片上色失败，请重试");
        }
        return R.ok(colourize);
    }

    @PostMapping("/matting")
    public R matting(@RequestBody ExploreDto exploreDto) {
        exploreDto.setUserId(Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        String matting = otherApiService.matting(exploreDto);
        if(null==matting){
            return R.no("图片抠图失败，请重试");
        }
        return R.ok(matting);
    }


    @PostMapping("/generateLayoutPhotos")
    public R generateLayoutPhotos(@RequestBody ExploreDto exploreDto) {
        exploreDto.setUserId(Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        String generateLayoutPhotos = otherApiService.generateLayoutPhotos(exploreDto);
        if(null==generateLayoutPhotos){
            return R.no("图片制作失败，请重试");
        }
        return R.ok(generateLayoutPhotos);
    }

    @PostMapping("/cartoon")
    public R cartoon(@RequestBody ExploreDto exploreDto) {
        exploreDto.setUserId(Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        String cartoon = otherApiService.cartoon(exploreDto);
        if(null==cartoon){
            return R.no("图片制作失败，请重试");
        }
        return R.ok(cartoon);
    }

    @PostMapping("/editImage")
    public R editImage(@RequestBody ExploreDto exploreDto) {
        exploreDto.setUserId(Integer.parseInt(StpUtil.getTokenInfo().getLoginId().toString()));
        String editImage = otherApiService.editImage(exploreDto);
        if(null==editImage){
            return R.no("图片编辑失败，请重试");
        }
        return R.ok(editImage);
    }


    /**
     * 将图片路径的文件转成 base64 编码并调用卡通化接口
     */
    @GetMapping("/cartoonFromImagePath")
    public R cartoonFromImagePath() {
        try {
            // 读取图片文件并编码为 Base64
            byte[] imageBytes = Files.readAllBytes(Paths.get("D:\\Desktop\\20250514112525.jpg"));
//            byte[] imageBytes = Files.readAllBytes(Paths.get("D:\\Desktop\\壁纸\\我的\\17471286343967.png"));
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 构造 ExploreDto
            ExploreDto exploreDto = new ExploreDto();
            exploreDto.setUserId(1);
//            exploreDto.setProcessedImage(base64Image);
            exploreDto.setProcessedImage("data:image/jpeg;base64," +base64Image);

            // 调用服务层处理
//            String result = otherApiService.cartoon(exploreDto);
            String result = otherApiService.colourize(exploreDto);
            if (result == null) {
                return R.no("图片制作失败，请重试");
            }
            return R.ok(result);
        } catch (Exception e) {
            return R.no("处理图片时发生错误: " + e.getMessage());
        }
    }


}
