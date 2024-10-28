package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.common.util.ResultCheckUtil;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String login(String code) {
        Long id = ResultCheckUtil.checkCode(driverInfoFeignClient.login(code));
        String token= UUID.randomUUID().toString().replaceAll("-","");
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token,id.toString(),RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public DriverLoginVo getDriverLoginInfo(HttpServletRequest httpServletRequest) {
        Long userId = AuthContextHolder.getUserId();
        DriverLoginVo driverLoginVo = ResultCheckUtil.checkCode(driverInfoFeignClient.getDriverInfo(userId));
        return driverLoginVo;
    }
}
