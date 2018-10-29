package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity,String> {

    @Query(value = "select * from user_entity where phone = ?1",nativeQuery = true)
    UserEntity getOne(String s);

    @Query(value = "SELECT user_entity.phone,user_entity.`password`,user_entity.username from user_entity ,user_role_entity where user_entity.phone=user_role_entity.phone and user_role_entity.role_id=?1",nativeQuery = true)
    List<UserEntity> getAllUserByRoleId(int roleId);

    @Query(value = "select u from UserEntity u where u.username like %?1%")
    List<UserEntity> findAllByUsernameContaining(String username);
}

