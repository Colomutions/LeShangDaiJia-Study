package com.atguigu.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.atguigu.daijia.common.Exception.BusinessException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.ModelUtils;
import com.atguigu.daijia.customer.mapper.CustomerInfoMapper;
import com.atguigu.daijia.customer.mapper.CustomerLoginLogMapper;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Wrapper;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private CustomerInfoMapper customerInfoMapper;
    @Autowired
    private CustomerLoginLogMapper customerLoginLogMapper;

    /**
     * @Author: Colomutions
     * @Description: 微信小程序登录
     * @Date: 2024/9/2 下午5:04
     * @Parms: [java.lang.String]
     * @ReturnType: com.atguigu.daijia.model.entity.customer.CustomerInfo
     */
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
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setWxOpenId(openid);
        LambdaQueryWrapper<CustomerInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CustomerInfo::getWxOpenId, openid);
        CustomerInfo isExist = customerInfoMapper.selectOne(lambdaQueryWrapper);
        if (Objects.nonNull(isExist)) {
            log.info("此用户并不是第一次登录，查询用户信息");
        } else {
            isExist=new CustomerInfo();
            isExist.setNickname(String.valueOf(System.currentTimeMillis()));
            isExist.setWxOpenId(sessionInfo.getOpenid());
             customerInfoMapper.insert(isExist);
        }
//        记录登录日志
        CustomerLoginLog customerLoginLog=new CustomerLoginLog();
        customerLoginLog.setCustomerId(isExist.getId());
        customerLoginLog.setCreateTime(new Date());
        customerLoginLog.setMsg("微信小程序登录");
        customerLoginLogMapper.insert(customerLoginLog);

//         返回用户的id
        return isExist.getId();
    }

    @Override
    public CustomerLoginVo getCustomerInfoById(Long customerId) {
        CustomerInfo customerInfo = customerInfoMapper.selectById(customerId);
        ModelUtils.nonNull(customerInfo,"不存在的用户");
        CustomerLoginVo customerLoginVo=new CustomerLoginVo();
        BeanUtils.copyProperties(customerInfo,customerLoginVo);
        boolean bindPhone = StringUtils.hasText(customerInfo.getPhone());
        customerLoginVo.setIsBindPhone(bindPhone);
        return customerLoginVo;
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) throws WxErrorException {
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(updateWxPhoneForm.getCode());
        if(Objects.isNull(phoneNoInfo)){
            throw new BusinessException("通过用户码值获取用户手机号失败");
        }
        CustomerInfo customerInfo = customerInfoMapper.selectById(updateWxPhoneForm.getCustomerId());
        if(Objects.isNull(customerInfo)){
            throw new BusinessException("不存在的用户，请联系管理员");
        }
        customerInfo.setPhone(phoneNoInfo.getPhoneNumber());
        customerInfoMapper.updateById(customerInfo);
        return true;
    }
}
