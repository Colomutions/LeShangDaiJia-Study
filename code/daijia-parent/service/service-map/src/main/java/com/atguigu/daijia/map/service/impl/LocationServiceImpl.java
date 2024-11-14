package com.atguigu.daijia.map.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Author: Colomutions
     * @Description:  上传司机的具体位置到redis中
     * @Date: 2024/11/14 下午2:49
     * @Parms: [com.atguigu.daijia.model.form.map.UpdateDriverLocationForm]
     * @ReturnType: java.lang.Boolean
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Point point=new Point(updateDriverLocationForm.getLongitude().doubleValue(),updateDriverLocationForm.getLatitude().doubleValue());
//        key的名称 经纬度 司机的id
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION,point,updateDriverLocationForm.getDriverId().toString());
        return true;
    }
    /**
     * @Author: Colomutions
     * @Description:  在司机停止接单后，删除redis中关于司机的位置
     * @Date: 2024/11/14 下午2:50
     * @Parms: [java.lang.Long]
     * @ReturnType: java.lang.Boolean
     */
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION,driverId.toString());
        return true;
    }
}
