package com.atguigu.daijia.common.util;

import com.atguigu.daijia.common.Exception.BusinessException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class ModelUtils {

    public static void nonNull( Object object, String message ) {
        if(object == null){
            throw new BusinessException(message);
        }
    }
    public static void isNull( Object object, String message ) {
        if(object != null){
            throw new BusinessException(message);
        }
    }

    public static String trimString(String str) {
        nonNull(str, "字符串不能为空");
        str=str.trim();
        return str;
    }

    public static String trimString(String str, String errorMessage) {
        nonNull(str, errorMessage);
        str=str.trim();
        return str;
    }

    public static void nonEmpty(List arrayList, String message ) {
        if(CollectionUtils.isEmpty(arrayList)){
            throw  new BusinessException(message);
        }
    }

    public static void nonEmpty(List arrayList) {
        if(CollectionUtils.isEmpty(arrayList)){
            throw  new BusinessException("集合不能为空");
        }
    }

    public static void isEmpty(List arrayList, String message ) {
        if(!CollectionUtils.isEmpty(arrayList)){
            throw  new BusinessException(message);
        }
    }

    public static void isEmpty(List arrayList) {
        if(!CollectionUtils.isEmpty(arrayList)){
            throw  new BusinessException("集合不能为空");
        }
    }

    public static void nonTrue(boolean date, String message ) {
        if(date){
            throw  new BusinessException(message);
        }
    }

    public static void nonTrue(boolean date) {
        if(date){
            throw  new BusinessException("条件不满足");
        }
    }

    public static void isTrue(boolean date, String message ) {
        if(!date){
            throw  new BusinessException(message);
        }
    }

    public static void isTrue(boolean date) {
        if(!date){
            throw  new BusinessException("条件不满足");
        }
    }
    public static void nonEquals(Object object1, Object object2, String message) {
        if(!Objects.equals(object1, object2)){
            throw  new BusinessException(message);
        }
    }

    public static void nonEquals(Object object1, Object object2) {
        if(!Objects.equals(object1, object2)){
            throw  new BusinessException("对象不相等");
        }
    }

    public static void isEquals(Object object1, Object object2, String message) {
        if(Objects.equals(object1, object2)){
            throw  new BusinessException(message);
        }
    }

    public static void isEquals(Object object1, Object object2) {
        if(Objects.equals(object1, object2)){
            throw  new BusinessException("对象相等");
        }
    }
}
