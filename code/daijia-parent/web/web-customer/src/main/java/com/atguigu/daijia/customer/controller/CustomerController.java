package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * @Author: Colomutions
     * @Description:  小程序登录，返回token
     * @Date: 2024/9/2 下午6:47
     * @Parms: [java.lang.String]
     * @ReturnType: com.atguigu.daijia.common.result.Result<java.lang.String>
     */
    @Operation(summary = "小程序授权登录接口")
    @GetMapping("/login/{code}")
    public Result<String> wxLogin(@PathVariable("code") String code) {
        return Result.ok(customerService.login(code));
    }

}

