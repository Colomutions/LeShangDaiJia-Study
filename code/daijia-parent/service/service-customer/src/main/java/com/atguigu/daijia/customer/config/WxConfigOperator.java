package com.atguigu.daijia.customer.config;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import cn.binarywang.wx.miniapp.api.WxMaService;


@Component
public class WxConfigOperator {
    @Autowired
    private WxConfigProperties wxConfigProperties;

//    配置微信接口访问令牌
    @Bean
    public WxMaService wxMaConfig() {
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(wxConfigProperties.getAppId());
        wxMaConfig.setSecret(wxConfigProperties.getSecret());
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }
}
