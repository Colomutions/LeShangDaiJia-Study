package com.atguigu.daijia.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.driver.mapper.DriverAccountMapper;
import com.atguigu.daijia.driver.mapper.DriverInfoMapper;
import com.atguigu.daijia.driver.mapper.DriverLoginLogMapper;
import com.atguigu.daijia.driver.mapper.DriverSetMapper;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.entity.driver.DriverAccount;
import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.entity.driver.DriverLoginLog;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}