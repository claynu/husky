package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.table.UserEntity;

public interface RedisForUserService {
    //编程规范问题，接口化的编程为的就是将实现封装起来，
    //然调用者只关心接口不关心实现，也就是“高内聚，低耦合”的思想。

    //增删查改
    Result getUser(String phone);

    Result save(UserEntity userEntity);

    Result updateUser(UserEntity userEntity);

    Result deleteUser(String phone);



}
