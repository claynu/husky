package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity,String> {

    @Query(value = "select ur.roleId from UserRoleEntity ur WHERE ur.phone = ?1 ")
    int getRoleIDByUserPhone(String phone);


    List<UserRoleEntity> findAllByRoleId(int roleId);
}
