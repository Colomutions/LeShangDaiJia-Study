package com.atguigu.daijia.driver.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.driver.config.TencentCloudProperties;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Autowired
    private TencentCloudProperties tencentCloudProperties;


    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        String bucketPrivate = tencentCloudProperties.getBucketPrivate();
        COSClient cosClient = this.constructCosClient();
        ObjectMetadata metadata=new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentEncoding("UTF-8");
        metadata.setContentType(file.getContentType());

        String fileType=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uploadPath="/driver/"+path+"/"+ UUID.randomUUID().toString().replaceAll("-","")+fileType;
        PutObjectRequest putObjectRequest=null;
        try{
            putObjectRequest=new PutObjectRequest(bucketPrivate,uploadPath,file.getInputStream(),metadata);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        putObjectRequest.setStorageClass(StorageClass.Standard);
        PutObjectResult putObjectResult=cosClient.putObject(putObjectRequest);
        log.info(JSON.toJSONString(putObjectResult));
        cosClient.shutdown();
        CosUploadVo cosUploadVo=new CosUploadVo();
        String imageUrl = this.getImageUrl(uploadPath);
        cosUploadVo.setShowUrl(imageUrl);
        cosUploadVo.setUrl(uploadPath);
        return cosUploadVo;
    }
/**
 * @Author: Colomutions
 * @Description:  构建cosClient对象
 * @Date: 2024/10/29 下午4:58
 * @Parms: []
 * @ReturnType: com.qcloud.cos.COSClient
 */
    public COSClient constructCosClient() {
        String secretId = tencentCloudProperties.getSecretId();
        String secretKey = tencentCloudProperties.getSecretKey();

        COSCredentials cosCredentials=new BasicCOSCredentials(secretId,secretKey);
        Region region=new Region(tencentCloudProperties.getRegion());
        ClientConfig clientConfig=new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cosCredentials,clientConfig);
    }
    /**
     * @Author: Colomutions
     * @Description:  获取图片的url，用于临时访问
     * @Date: 2024/10/29 下午4:43
     * @Parms: [java.lang.String]
     * @ReturnType: java.lang.String
     */
    @Override
    public String getImageUrl(String path) {
        if(!StringUtils.hasText(path)) return "";
        COSClient cosClient = this.constructCosClient();
        GeneratePresignedUrlRequest request=new GeneratePresignedUrlRequest(tencentCloudProperties.getBucketPrivate(), path, HttpMethodName.GET);
        Date expiration = Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
        request.setExpiration(expiration);
        URL url = cosClient.generatePresignedUrl(request);
        cosClient.shutdown();
        return url.toString();
    }
}
