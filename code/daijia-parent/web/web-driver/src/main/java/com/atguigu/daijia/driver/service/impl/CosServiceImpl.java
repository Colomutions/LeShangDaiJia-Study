package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.util.ResultCheckUtil;
import com.atguigu.daijia.driver.client.CosFeignClient;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Autowired
    private CosFeignClient cosFeignClient;


    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        CosUploadVo upload = ResultCheckUtil.checkCode(cosFeignClient.upload(file, path));
        log.info("此处feign传回来的上传数据为：{}" , upload);
        return upload;
    }
}
