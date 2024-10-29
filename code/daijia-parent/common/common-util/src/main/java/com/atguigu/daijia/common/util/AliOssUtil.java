package com.atguigu.daijia.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.utils.StringUtils;
import com.atguigu.daijia.common.Exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "alioss")
public class AliOssUtil {

    private static final Logger log = LoggerFactory.getLogger(AliOssUtil.class);
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {
        InputStream inputStream = null;
        String uploadImageUrl=null;
        try {
            inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            if(StringUtils.isEmpty(originalFilename)){
                throw new BusinessException("传输过来的文件名为空，无法进行处理");
            }
            int index = originalFilename.lastIndexOf(".");
            String extname = originalFilename.substring(index);
            String filename = UUID.randomUUID().toString() + extname;

            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, filename, inputStream);
            uploadImageUrl = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + filename;
            ossClient.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(Objects.nonNull(inputStream)){
                inputStream.close();
            }
        }

        return uploadImageUrl;
    }
}