package com.atguigu.daijia.map.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.daijia.common.Exception.BusinessException;
import com.atguigu.daijia.common.util.ModelUtils;
import com.atguigu.daijia.map.service.MapService;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapServiceImpl implements MapService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("tencent.cloud.map")
    private String key;


    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        String url="https://apis.map.qq.com/ws/direction/v1/driving/?from={from}&to={to}&key=[{key}]";
        Map<String,String> map=new HashMap<>();
        map.put("from",calculateDrivingLineForm.getStartPointLatitude()+","+calculateDrivingLineForm.getStartPointLongitude());
        map.put("to",calculateDrivingLineForm.getEndPointLatitude()+","+calculateDrivingLineForm.getEndPointLongitude());
        map.put("key",key);
        JSONObject forObject = restTemplate.getForObject(url, JSONObject.class, map);
        ModelUtils.nonNull(forObject,"调用tencent地图API返回值为空");
        DrivingLineVo drivingLineVo=null;
        if(forObject.getInteger("status").equals(0)){
            log.info("调用tencent地图API成功");
            drivingLineVo=new DrivingLineVo();
            JSONObject jsonObject = forObject.getJSONObject("result").getJSONArray("routes").getJSONObject(0);
            drivingLineVo.setDistance(jsonObject.getBigDecimal("distance").divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP));
            drivingLineVo.setDuration(jsonObject.getBigDecimal("duration"));
            drivingLineVo.setPolyline(jsonObject.getJSONArray("polyline"));
        }else{
            log.error("调用tencent地图API失败");
            log.info(forObject.toString());
        }
        if(drivingLineVo==null){
            return drivingLineVo;
        }else{
            throw new BusinessException("无法正常获取腾讯地图api数据");
        }
    }
}
