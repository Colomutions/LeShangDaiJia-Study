package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value="/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Operation(summary = "司机登录相关接口")
    @GetMapping("/login/{code}")
    Result<String> login(@PathVariable("code") String code){
        return Result.ok(driverService.login(code));
    }

    @Operation(summary = "获取司机信息相关接口")
    @GetMapping("/getDriverLoginInfo")
    @LoginCheck
    Result<DriverLoginVo> getDriverLoginInfo(HttpServletRequest httpServletRequest){
        return Result.ok(driverService.getDriverLoginInfo(httpServletRequest));
    }

}

