package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;

public interface MessageService {

    Result send2Customer(String name, String frontPhone, String customerPhone);
    Result send2Tech(String techName, String techPhone);

    Result send2Merchant(String merchantName, String merchantPhone);
    Result merchantSend2Customer(String name, String customerPhone,String merchantPhone);

}
