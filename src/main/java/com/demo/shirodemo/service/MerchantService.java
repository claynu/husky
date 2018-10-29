package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForMerchant;

public interface MerchantService {
    ResultForMerchant getAllInfo();
    Result findSoftById(int id);
    Result findSoftByIdAndVersion(int id,String version);
    Result findScreenById(int id);
    Result findBoardById(int id);
}
