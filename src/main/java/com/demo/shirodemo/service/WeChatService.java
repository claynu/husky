package com.demo.shirodemo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.WeChatEntity;
import springfox.documentation.spring.web.json.Json;

public interface WeChatService {
    /**
     * 微信用户的注册
     * 接收code去获取openId   appId appSecret code   --->session_key   openId
     * 获取之后判断openId 是否存在    存在则登录成功   不存在则提示注册
     * 登录
     *
     */

    Result addOne(WeChatEntity weChatEntity);
    Result UpdateOne(WeChatEntity weChatEntity);
    Boolean isExist(String openId);
    WeChatEntity findByOpenId(String openId);
    String getOpenId(String code);
    String getWechatToken();
    Boolean sendWeChatMessage(JSONObject jsonObject);
    WeChatEntity findByPhone(String phone);
    WeChatEntity findByStudentId(String studentId);

}
