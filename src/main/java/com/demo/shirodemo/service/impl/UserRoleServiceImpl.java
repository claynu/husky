package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.UserRoleRepository;
import com.demo.shirodemo.entity.table.UserRoleEntity;
import com.demo.shirodemo.service.UserRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    UserRoleRepository userRoleRepository;
    @Override
    public int getRoleIDByUserPhone(String phone){
        return userRoleRepository.getRoleIDByUserPhone(phone);
    }

    @Override
    public void save(UserRoleEntity userRoleEntity) {
        userRoleRepository.save(userRoleEntity);
    }

    @Override
    public List<UserRoleEntity> getListByRoleId(int id) {
        return userRoleRepository.findAllByRoleId(id);
    }
}
