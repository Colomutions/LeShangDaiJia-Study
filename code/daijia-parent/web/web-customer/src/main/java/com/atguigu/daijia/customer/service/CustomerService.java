package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {


    String login(String code);

    CustomerLoginVo getCustomerLoginInfo(HttpServletRequest request);

    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);
}
