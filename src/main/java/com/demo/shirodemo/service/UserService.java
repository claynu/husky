package com.demo.shirodemo.service;

import com.demo.shirodemo.entity.Result;
import com.demo.shirodemo.entity.ResultForLogin;
import com.demo.shirodemo.entity.table.UserEntity;
import com.demo.shirodemo.entity.table.UserRoleEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    UserEntity findByPhone(String phone);

    void addOneUser(UserEntity userEntity);

    Boolean deleteUser(String phone);

    ResultForLogin login(String phone, String password);

    Result findAll();
    List<UserEntity> findAllByNameLike(String username);

    Result register(UserEntity u, UserRoleEntity ur);

    Result resetPassword(String phone);

    Result changePassword(String phone,String password);

    Result getAllByRole(String role);
    Result getAllByRoleId(int roleId);

    Result addInsider(UserEntity userEntity,int roleId);
}
