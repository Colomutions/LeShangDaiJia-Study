package com.atguigu.daijia.map.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.ResultCheckUtil;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    /**
     * @Author: Colomutions
     * @Description: 上传司机的具体位置到redis中
     * @Date: 2024/11/14 下午2:49
     * @Parms: [com.atguigu.daijia.model.form.map.UpdateDriverLocationForm]
     * @ReturnType: java.lang.Boolean
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());
//        key的名称 经纬度 司机的id
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION, point, updateDriverLocationForm.getDriverId().toString());
        return true;
    }

    /**
     * @Author: Colomutions
     * @Description: 在司机停止接单后，删除redis中关于司机的位置
     * @Date: 2024/11/14 下午2:50
     * @Parms: [java.lang.Long]
     * @ReturnType: java.lang.Boolean
     */
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());
        return true;
    }

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {
//        定义经纬位置
        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(), searchNearByDriverForm.getLatitude().doubleValue());
//        定义搜索半径5公里
        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS);
//        创建circle对象，实现圆
        Circle circle = new Circle(point, distance);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates()//坐标
                .includeDistance()//距离
                .sortAscending();//按照距离升序
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();
        List<NearByDriverVo> list = new ArrayList<>();
        Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();
        while (iterator.hasNext()) {
            GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

            //获取司机id
            Long driverId = Long.parseLong(item.getContent().getName());

            //远程调用，根据司机id个性化设置信息
            DriverSet driverSet = ResultCheckUtil.checkCode(driverInfoFeignClient.getDriverSet(driverId));
            BigDecimal orderDistance = driverSet.getOrderDistance();
            if (orderDistance.doubleValue() != 0
                    && orderDistance.subtract(searchNearByDriverForm.getMileageDistance()).doubleValue() < 0) {
                continue;
            }
            BigDecimal currentDistance =
                    new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal acceptDistance = driverSet.getAcceptDistance();
            if (acceptDistance.doubleValue() != 0
                    && acceptDistance.subtract(currentDistance).doubleValue() < 0) {
                continue;
            }
            NearByDriverVo nearByDriverVo = new NearByDriverVo();
            nearByDriverVo.setDriverId(driverId);
            nearByDriverVo.setDistance(currentDistance);
            list.add(nearByDriverVo);
        }
        return list;
    }
}
