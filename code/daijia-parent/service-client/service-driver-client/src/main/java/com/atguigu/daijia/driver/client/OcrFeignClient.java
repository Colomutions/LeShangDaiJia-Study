package com.atguigu.daijia.driver.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "service-driver")
public interface OcrFeignClient {

    /**
     * @Author: Colomutions
     * @Description:  身份证识别上传接口
     * @Date: 2024/10/30 上午10:11
     * @Parms: [org.springframework.web.multipart.MultipartFile]
     * @ReturnType: com.atguigu.daijia.common.result.Result<com.atguigu.daijia.model.vo.driver.IdCardOcrVo>
     */
    @PostMapping(value = "ocr/idCardOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   Result<IdCardOcrVo> idCardOcr(@RequestPart("file") MultipartFile file);

    /**
     * @Author: Colomutions
     * @Description:  驾驶证识别上传接口
     * @Date: 2024/10/30 上午10:57
     * @Parms: [org.springframework.web.multipart.MultipartFile]
     * @ReturnType: com.atguigu.daijia.common.result.Result<com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo>
     */
    @PostMapping(value = "/ocr/driverLicenseOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<DriverLicenseOcrVo> driverLicenseOcr(@RequestPart("file") MultipartFile file);

}