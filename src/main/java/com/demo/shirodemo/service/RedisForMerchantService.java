package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;

public interface RedisForMerchantService {
    Result findSoftByIdAndVersion(int id, String version);

}

