package com.atguigu.daijia.customer.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

/**
 * @Author: Colomutions
 * @Description:  微信小程序登录远程调用接口
 * @Date: 2024/9/2 下午6:42
 * @Parms: [java.lang.String]
 * @ReturnType: com.atguigu.daijia.common.result.Result<java.lang.Long>
 */
    @GetMapping("/customer/info/wxLogin/{code}")
    Result<Long> wxLogin(@PathVariable String code);

/**
 * @Author: Colomutions
 * @Description:  根据token获取用户信息
 * @Date: 2024/9/3 上午9:21
 * @Parms: [java.lang.Long]
 * @ReturnType: com.atguigu.daijia.common.result.Result<com.atguigu.daijia.model.entity.customer.CustomerInfo>
 */
    @GetMapping("/customer/info/getCustomerLoginInfo/{customerId}")
     Result<CustomerLoginVo> getCustomerInfo(@PathVariable Long customerId);

    /**
     * @Author: Colomutions
     * @Description:  根据code值，获取当前用户的手机号更新到个人信息中
     * @Date: 2024/10/28 下午3:15
     * @Parms: [com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm]
     * @ReturnType: com.atguigu.daijia.common.result.Result<java.lang.Boolean>
     */
    @PostMapping("/customer/info/updateWxPhoneNumber")
    Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm);
}