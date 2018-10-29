package com.demo.shirodemo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.shirodemo.dao.WeChatRepository;
import com.demo.shirodemo.entity.OpenId;
import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.WeChatEntity;
import com.demo.shirodemo.schedule.WeChatToken;
import com.demo.shirodemo.service.RedisForUserService;
import com.demo.shirodemo.service.UserRoleService;
import com.demo.shirodemo.service.UserService;
import com.demo.shirodemo.service.WeChatService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    WeChatRepository repository;
    @Autowired
    UserService userService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RedisForUserService redisForUserService;
    @Override
    public Result addOne(WeChatEntity weChatEntity) {
        try {
            repository.save(weChatEntity);
            return new Result(true,"添加成功",weChatEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"数据库操作失败");
        }
    }

    @Transactional
    @Override
    public Result UpdateOne(WeChatEntity weChatEntity) {
        try {
            if (isExist(weChatEntity.getOpenId())) {
                repository.save(weChatEntity);
                UserEntity u = userService.findByPhone(weChatEntity.getPhone());
                u.setUsername(weChatEntity.getUsername());
                redisForUserService.updateUser(u);
                return new Result(true, "更新成功", weChatEntity);
            }
            return new Result(false,"不存在该信息");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"数据库操作失败");
        }
    }

    @Override
    public Boolean isExist(String openId) {
        return findByOpenId(openId) != null;
    }

    @Override
    public WeChatEntity findByOpenId(String openId) {
        try {
            return repository.findByOpenId(openId);
        } catch (Exception e) {
            return null;

        }
    }

    @Override
    public String getOpenId(String code) {
        RestTemplate restTemplate=new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx2c72530223447bcc&secret=eb9d04821f27dd3f7af9994c5db5f5b5&js_code="+code+"&grant_type=authorization_code";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return JSONObject.parseObject(response.getBody().toString(),OpenId.class).getOpenid();
        }
        else{
            System.out.println(response.getBody().toString());
            return null;
        }


    }
    @Override
    public String getWechatToken() {
        RestTemplate restTemplate=new RestTemplate();
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2c72530223447bcc&secret=eb9d04821f27dd3f7af9994c5db5f5b5";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().split("\"")[3];
        }
        else{
            return null;
        }


    }



    @Override
    public WeChatEntity findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    @Override
    public WeChatEntity findByStudentId(String studentId) {
        return repository.findByStudentId(studentId);
    }

    @Override
    public Boolean sendWeChatMessage(JSONObject json) {
        RestTemplate restTemplate=new RestTemplate();
        String token = json.getString("access_token");
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="+token;
        ResponseEntity<String> response = restTemplate.postForEntity(url,json,String.class);
        log.error(json.toString());
        if(response.getStatusCode().is2xxSuccessful()){
            System.out.println(response.getBody());
            return true;
        }
        else{
            return false;
        }
    }


}
