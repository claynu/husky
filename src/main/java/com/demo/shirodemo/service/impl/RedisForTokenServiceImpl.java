package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.service.RedisForTokenService;
import com.demo.shirodemo.tool.CryptographyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class RedisForTokenServiceImpl implements RedisForTokenService {

    @Resource
    StringRedisTemplate redisTemplateForString;

    public String addUserToken(String phone) {
        String key = "token:"+phone;
        Boolean hasKey = redisTemplateForString.hasKey(key);
        String value = CryptographyUtil.getUserToken(key);
        try {
            redisTemplateForString.opsForValue().set(key, value, 600, TimeUnit.SECONDS);
            return value;
        } catch (Exception e) {
            System.out.println("append token false");
            return null;
        }
    }

    public String delUserToken(String phone) {
        String key = "token:"+phone;
        Boolean hasKey = redisTemplateForString.hasKey(key);
        if (hasKey) {
            redisTemplateForString.delete(key);
            return "缓存已删除";
        } else {
            return "您还未登录呢---令牌不存在";
        }
    }

    @Override
    public Result checkToken(String token) {
        Result result = new Result();
        try {
            UserEntity u = (UserEntity) SecurityUtils.getSubject().getPrincipal();
            String key = "token:"+u.getPhone();
            Boolean hasKey = redisTemplateForString.hasKey(key);
            if (hasKey) {
                 result.setSuccess(redisTemplateForString.opsForValue().get(key).equals(token));
                 if (!result.getSuccess()){
                     log.error(redisTemplateForString.opsForValue().get(key));
                     result.setMsg("令牌失效");
                 }
            } else {
               result.setSuccess(false);
               result.setMsg("登录信息已失效");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg("登录信息已失效");
        }
        return result;
    }
}