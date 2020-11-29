package com.yiyi.service.global.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author : Jack.F
 * @date : 2020/9/22
 */

public class FileOssUtils {

    public static String uploadFile(MultipartFile file,String model) throws IOException {
        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
        String accessKeyId = "LTAI4GAhhYnkLydd2wxTvo3x";
        String accessKeySecret = "7fVDtNg4q1D2wlmSoYqoTNaKyzslkQ";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String bucketName = "yiyi-file";
        String dateTime = new DateTime().toString("yyyy/MM/dd");
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String prefix = UUID.randomUUID().toString().replace("-", "");
        String objectname = model+"/"+dateTime+"/"+prefix+fileType;
        ossClient.putObject(bucketName, objectname, file.getInputStream());
        ossClient.shutdown();
        //https://yiyi-file.oss-cn-shenzhen.aliyuncs.com/user_avatar/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg
        return "https://"+bucketName+"."+ "oss-cn-shenzhen.aliyuncs.com/"+dateTime+"/"+prefix+fileType;
    }
}
