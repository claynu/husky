package com.demo.shirodemo.schedule;

import com.demo.shirodemo.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;

@Slf4j
@Component
public class WeChatToken {
    @Autowired
    private WeChatService weChatService;

    private static String token;

    @Scheduled(fixedDelay = 10000,initialDelay = 0)
    public void getToken(){
        token = weChatService.getWechatToken();
        log.info(new Time(System.currentTimeMillis()).toString());
        log.info(token);
    }






    public static String getWeChatToken(){
        return token;
    }
}
