package com.demo.shirodemo.service;


import com.demo.shirodemo.entity.Result;

public interface RedisForTokenService {

    String addUserToken(String key);

    String delUserToken(String key);

    Result checkToken(String token);

}
