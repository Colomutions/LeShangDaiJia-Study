package com.atguigu.daijia.common.Exception;

import com.atguigu.daijia.common.result.ResultCodeEnum;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    public static Integer DEFAULT_ERROR_CODE = ResultCodeEnum.SERVICE_ERROR.getCode();


    private final Integer code;

    private final String msg;

    public BusinessException(String msg) {
        super(msg);
        this.code = DEFAULT_ERROR_CODE;
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}