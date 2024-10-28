package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import jakarta.servlet.http.HttpServletRequest;

public interface DriverService {


    String login(String code);

    DriverLoginVo getDriverLoginInfo(HttpServletRequest httpServletRequest);
}
