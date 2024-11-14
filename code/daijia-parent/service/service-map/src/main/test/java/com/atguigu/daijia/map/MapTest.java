package com.atguigu.daijia.map;

import com.alibaba.fastjson.JSON;
import com.atguigu.daijia.model.form.rules.FeeRuleRequest;

import com.atguigu.daijia.model.vo.rules.FeeRuleResponse;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MapTest {

    @Value("${tencent.cloud.map.key}")
    private String key;

    @Test
    void test1() {
        System.out.println(key);
    }

}
