package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.ResultCheckUtil;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.map.client.MapFeignClient;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.atguigu.daijia.rules.client.FeeRuleFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MapFeignClient mapFeignClient;
    @Autowired
    private FeeRuleFeignClient feeRuleFeignClient;
    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;


    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {
        CalculateDrivingLineForm calculateDrivingLineForm=new CalculateDrivingLineForm();
        BeanUtils.copyProperties(expectOrderForm,calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = ResultCheckUtil.checkCode(mapFeignClient.calculateDrivingLine(calculateDrivingLineForm));
        FeeRuleRequestForm form = new FeeRuleRequestForm();
        form.setDistance(drivingLineVo.getDistance());
        form.setWaitMinute(0);
        form.setStartTime(new Date());
        FeeRuleResponseVo feeRuleResponseVo = ResultCheckUtil.checkCode(feeRuleFeignClient.calculateOrderFee(form));
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);
        return expectOrderVo;
    }

    //乘客查找当前订单
    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        return ResultCheckUtil.checkCode(orderInfoFeignClient.searchCustomerCurrentOrder(customerId));
    }
}
