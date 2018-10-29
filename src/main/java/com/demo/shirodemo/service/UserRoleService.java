package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.table.UserRoleEntity;

import java.util.List;

public interface UserRoleService {

    int getRoleIDByUserPhone(String phone);

    void save(UserRoleEntity userRoleEntity);

    List<UserRoleEntity> getListByRoleId(int id);
}
