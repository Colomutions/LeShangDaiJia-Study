package com.atguigu.daijia.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum OrderType {
    CHAUFFEUR_ORDER(1, "DJD","代驾订单"),
    ;

    @EnumValue
    private Integer code;
    private String desc;
    private String desception;

    OrderType(Integer code, String desc,String desception) {
        this.code = code;
        this.desc = desc;
        this.desception = desception;
    }

}
