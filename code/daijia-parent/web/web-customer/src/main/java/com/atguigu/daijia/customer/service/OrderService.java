package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);
}
