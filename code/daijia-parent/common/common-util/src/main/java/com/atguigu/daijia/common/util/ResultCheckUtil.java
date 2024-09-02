package com.atguigu.daijia.common.util;

import com.atguigu.daijia.common.Exception.BusinessException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;

import java.util.Objects;

public final class ResultCheckUtil {

    public static <T> T checkCode(Result<T> resultBody, String errorMsg) {
        ModelUtils.nonEquals(resultBody.getCode(), ResultCodeEnum.SUCCESS.getCode());
        return resultBody.getData();
    }

    public static <T> T checkCode(Result<T> resultBody) {
        ModelUtils.nonEquals(resultBody.getCode(), ResultCodeEnum.SUCCESS.getCode(),"远程调用异常，请联系管理员");
        return resultBody.getData();
    }

    public static <T> T checkCodeAndNonNull(Result<T> resultBody) {
        ModelUtils.nonEquals(resultBody.getCode(), ResultCodeEnum.SUCCESS.getCode(),"远程调用异常，请联系管理员");
        ModelUtils.nonNull(resultBody.getData(),"获取到的数据为空，请联系管理员");
        return resultBody.getData();
    }
    public static <T> T checkCodeAndNonNull(Result<T> resultBody,String errorMsg) {
        ModelUtils.nonEquals(resultBody.getCode(), ResultCodeEnum.SUCCESS.getCode(),"远程调用异常，请联系管理员");
        ModelUtils.nonNull(resultBody.getData(),errorMsg);
        return resultBody.getData();
    }
}
