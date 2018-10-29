package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.RoleRepository;
import com.demo.shirodemo.entity.table.RoleEntity;
import com.demo.shirodemo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository repository;


    public RoleEntity getOne(int s){
        return repository.getOne(s);
    }

    @Override
    public int findIdByName(String i) {
        return repository.findIdByName(i);
    }
}
