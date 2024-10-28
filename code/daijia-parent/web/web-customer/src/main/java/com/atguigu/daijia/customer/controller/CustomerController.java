package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getCustomerLoginInfo")
    @LoginCheck
    public Result<CustomerLoginVo> getCustomerLoginInfo(HttpServletRequest request) {
        return Result.ok(customerService.getCustomerLoginInfo(request));
    }

    @Operation(summary = "更新客户微信手机号码")
    @PostMapping("/updateWxPhone")
    public Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm){
        return Result.ok(customerService.updateWxPhoneNumber(updateWxPhoneForm));
    }


}

