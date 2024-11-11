package com.atguigu.daijia.order.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "订单API接口管理")
@RestController
@RequestMapping(value="/order/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoController {

    //TODO 后续完善，目前假设乘客当前没有订单
    @Operation(summary = "乘客端查找当前订单")
    @GetMapping("/searchCustomerCurrentOrder/{customerId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(@PathVariable Long customerId) {
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        currentOrderInfoVo.setIsHasCurrentOrder(false);
        return Result.ok(currentOrderInfoVo);
    }

}

