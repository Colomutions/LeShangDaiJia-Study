package com.atguigu.daijia.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.driver.config.TencentCloudProperties;
import com.atguigu.daijia.driver.mapper.DriverAccountMapper;
import com.atguigu.daijia.driver.mapper.DriverInfoMapper;
import com.atguigu.daijia.driver.mapper.DriverLoginLogMapper;
import com.atguigu.daijia.driver.mapper.DriverSetMapper;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.entity.driver.DriverAccount;
import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.entity.driver.DriverLoginLog;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonRequest;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private DriverInfoMapper driverInfoMapper;
    @Autowired
    private DriverLoginLogMapper driverLoginLogMapper;
    @Autowired
    private DriverSetMapper driverSetMapper;
    @Autowired
    private DriverAccountMapper driverAccountMapper;
    @Autowired
    private CosService cosService;
    @Autowired
    private TencentCloudProperties tencentCloudProperties;

    @Override
    public Long login(String code) {
        log.info("调用登录解析方法，传入的code为：{}", code);
//        调用微信工具包的登录方法，获取用户的openId
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            log.error("调用微信工具包获取微信用户信息失败");
            throw new RuntimeException(e);
        }
//        根据openId查询用户信息，如果是第一次登录就新增用户
        String openid = sessionInfo.getOpenid();
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setWxOpenId(openid);
        LambdaQueryWrapper<DriverInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DriverInfo::getWxOpenId, openid);
        DriverInfo isExist = driverInfoMapper.selectOne(lambdaQueryWrapper);
        if (Objects.nonNull(isExist)) {
            log.info("此用户并不是第一次登录，查询用户信息");
        } else {
            isExist=new DriverInfo();
            isExist.setNickname(String.valueOf(System.currentTimeMillis()));
            isExist.setWxOpenId(sessionInfo.getOpenid());
            driverInfoMapper.insert(isExist);

            DriverSet driverSet=new DriverSet();
            driverSet.setDriverId(isExist.getId());
            driverSet.setOrderDistance(BigDecimal.ZERO);
            driverSet.setAcceptDistance(new BigDecimal(SystemConstant.ACCEPT_DISTANCE));
            driverSet.setIsAutoAccept(0);
            driverSetMapper.insert(driverSet);

            DriverAccount driverAccount=new DriverAccount();
            driverAccount.setDriverId(isExist.getId());
            driverAccountMapper.insert(driverAccount);
        }
//        记录登录日志
        DriverLoginLog driverLoginLog=new DriverLoginLog();
        driverLoginLog.setDriverId(isExist.getId());
        driverLoginLog.setCreateTime(new Date());
        driverLoginLog.setMsg("微信小程序登录");
        driverLoginLogMapper.insert(driverLoginLog);

//         返回用户的id
        return isExist.getId();
    }

    @Override
    public DriverLoginVo getDriverInfo(Long driverId) {
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        DriverLoginVo driverLoginVo=new DriverLoginVo();
        BeanUtils.copyProperties(driverInfo,driverLoginVo);
        if(Objects.nonNull(driverInfo.getFaceModelId())){
            log.info("此处的用户的人脸模型已经识别，用户id为：{},人脸模型id为：{}",driverId,driverInfo.getFaceModelId());
            driverLoginVo.setIsArchiveFace(true);
        }else{
            driverLoginVo.setIsArchiveFace(false);
        }
        return driverLoginVo;
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        DriverAuthInfoVo driverAuthInfoVo = new DriverAuthInfoVo();
        BeanUtils.copyProperties(driverInfo,driverAuthInfoVo);
        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardBackUrl()));
        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardHandUrl()));
        driverAuthInfoVo.setDriverLicenseFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseFrontUrl()));
        driverAuthInfoVo.setDriverLicenseBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseBackUrl()));
        driverAuthInfoVo.setDriverLicenseHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseHandUrl()));
        driverAuthInfoVo.setDriverId(driverId);
        return driverAuthInfoVo;
    }

    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        //获取司机id
        Long driverId = updateDriverAuthInfoForm.getDriverId();

        //修改操作
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setId(driverId);
        BeanUtils.copyProperties(updateDriverAuthInfoForm,driverInfo);
        boolean update = this.updateById(driverInfo);
        return update;
    }

    //创建司机人脸模型
    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        DriverInfo driverInfo = driverInfoMapper.selectById(driverFaceModelForm.getDriverId());
        try{
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            IaiClient client = new IaiClient(cred, tencentCloudProperties.getRegion(),
                    clientProfile);
            CreatePersonRequest req = new CreatePersonRequest();
            req.setGroupId(tencentCloudProperties.getPersionGroupId());
            req.setPersonId(String.valueOf(driverInfo.getId()));
            req.setGender(Long.parseLong(driverInfo.getGender()));
            req.setQualityControl(4L);
            req.setUniquePersonControl(4L);
            req.setPersonName(driverInfo.getName());
            req.setImage(driverFaceModelForm.getImageBase64());
            CreatePersonResponse resp = client.CreatePerson(req);
            String faceId = resp.getFaceId();
            if(StringUtils.hasText(faceId)) {
                driverInfo.setFaceModelId(faceId);
                driverInfoMapper.updateById(driverInfo);
            }
        } catch (TencentCloudSDKException e) {
            log.error("处理过程中发生异常", e);
            return false;
        }
        return true;
    }
}