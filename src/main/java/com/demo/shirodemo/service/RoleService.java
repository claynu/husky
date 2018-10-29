package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.table.RoleEntity;

public interface RoleService {
    RoleEntity getOne(int i);
    int findIdByName(String i);
}
