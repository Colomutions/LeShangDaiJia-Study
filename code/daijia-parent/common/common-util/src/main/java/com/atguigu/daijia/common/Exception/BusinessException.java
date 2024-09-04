package com.atguigu.daijia.common.Exception;

import com.atguigu.daijia.common.result.ResultCodeEnum;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    public static Integer DEFAULT_ERROR_CODE = ResultCodeEnum.SERVICE_ERROR.getCode();


    private final Integer code;

    private final String message;

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    public BusinessException(String message) {
        super(message);
        this.code = DEFAULT_ERROR_CODE;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}