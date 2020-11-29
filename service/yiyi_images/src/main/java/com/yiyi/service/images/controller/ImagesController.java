package com.yiyi.service.images.controller;

import com.baidu.aip.ocr.AipOcr;
import com.yiyi.commom.base.result.R;
import com.yiyi.service.images.entity.JsonEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


@RestController
@RequestMapping("image")
public class ImagesController {

    //设置APPID/AK/SK
    public static final String APP_ID = "23043340";
    public static final String API_KEY = "NbZsvcMkU6XGGWfuvCtEtBGQ";
    public static final String SECRET_KEY = "FuU4WY6PeG8kbmtYPsOh3hWtty7yBTQr";

    @ApiOperation("文件上传接口")
    @GetMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        AipOcr client = aipOcr();
        JSONObject res = client.basicGeneral(bytes, options);
        JSONArray result = res.getJSONArray("words_result");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            JSONObject jsonObject = result.getJSONObject(i);
            String words = jsonObject.getString("words");
            stringBuilder.append(words);
        }

        return R.ok().data("item",stringBuilder.toString());
    }


    private AipOcr aipOcr(){
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        return client;
    }
}
